const crypto = require('crypto');
const jwt = require('jsonwebtoken')

const catchAsync = require('../utils/catchAsync')
const APIError = require('../utils/APIError');

const SMSclient = require('twilio')(process.env.TWILIO_ACCOUNT_SID, process.env.TWILIO_AUTH_TOKEN);
const redisClient = require('../config/initRedis');
const { inProduction } = require('../config/globalVars');
const User = require('../models/userModel');
const joi = require('joi')

function generateNewAccessToken(user) {
    const expiresIn = '1d'
    const accessToken = jwt.sign(user, process.env.JWT_ACCESS_TOKEN_SECRET, { expiresIn })
    return accessToken;
}

async function generateNewRefreshToken(user) {
    const refreshToken = jwt.sign(user, process.env.JWT_REFRESH_TOKEN_SECRET, { expiresIn: '1y' });
    const EX = 365 * 24 * 60 * 60;
    try {
        await redisClient.set(user.id, refreshToken, {
            EX
        });
    } catch (e) {
        throw new APIError(500, "Internal server error!");
    }

    return refreshToken;
}

async function verifyRefreshToken(refreshToken) {
    let user, token;
    try {
        user = await jwt.verify(refreshToken, process.env.JWT_REFRESH_TOKEN_SECRET);
    }
    catch (err) {
        throw new APIError(403, "Invalid refresh token!")
    }

    try {
        token = await redisClient.get(user.id)
    }
    catch (err) {
        console.log(err)
        throw new APIError(500, "Internal server error!")
    }

    if (token !== refreshToken) {
        throw new APIError(403, "Invalid refresh token!")
    }

    return user;
}



module.exports.sendOTP = catchAsync(async (req, res) => {
    const phone = req.body.phone;
    if (phone == null || phone == undefined) throw new APIError(400, "Missing Fields!")

    const otp = Math.floor(100000 + Math.random() * 900000);
    const ttl = 2 * 60 * 1000 //2 mins
    const expires = Date.now() + ttl;
    const hash = crypto.createHmac('sha256', process.env.OTP_HASH_SECRET).update(`${phone}.${otp}.${expires}`).digest('hex');
    const fullHash = `${hash}.${expires}`;

    if (inProduction) {
        await SMSclient.messages.create(
            {
                body: "Your One Time Password(OTP) for Chat App is " + otp,
                from: "+17792092313",
                to: phone
            }
        );
    }

    console.log(otp)
    res.send({ phone, hash: fullHash })
})

module.exports.verifyOTP = catchAsync(async (req, res) => {
    const { phone, hash, otp, publicKey } = req.body
    let { username } = req.body;

    const schema = joi.object({
        phone: joi.string().required(),
        hash: joi.string().required(),
        otp: joi.string().length(6).required(),
        publicKey: joi.string().required()
    })

    if (!username) {
        username = phone
    }

    await schema.validateAsync({ phone, hash, otp, publicKey })

    const [hashValue, expires] = hash.split('.');

    if (Date.now() > parseInt(expires)) {
        throw new APIError(504, "Timeout! Please try again")
    }

    const calculatedHash = crypto.createHmac('sha256', process.env.OTP_HASH_SECRET).update(`${phone}.${otp}.${expires}`).digest('hex');

    if (calculatedHash !== hashValue) {
        throw new APIError(401, "Verification failed! Incorrect OTP");
    }

    const user = await User.findOneAndUpdate({ phone }, { username, publicKey },
        {
            upsert: true,
            new: true,
            setDefaultsOnInsert: true
        });

    const accessToken = generateNewAccessToken({ id: user._id.toString() })

    const refreshToken = await generateNewRefreshToken({ id: user._id.toString() })

    res.send({ message: "Verification Successful!", accessToken, refreshToken, user })
})

module.exports.refreshToken = catchAsync(async (req, res, next) => {
    const { refreshToken } = req.body;
    if (refreshToken == undefined) throw new APIError(400, "Refresh token required!")

    const user = await verifyRefreshToken(refreshToken);

    const accessToken = generateNewAccessToken({ id: user.id })
    const newRefreshToken = await generateNewRefreshToken({ id: user.id });

    res.send({ accessToken, refreshToken: newRefreshToken })

})

module.exports.logOut = catchAsync(async (req, res) => {
    const { refreshToken } = req.body;
    if (!refreshToken) throw new APIError(400, "Refresh token required!")

    const user = await verifyRefreshToken(refreshToken)

    try {
        await redisClient.DEL(user.id)
    }
    catch (err) {
        throw new APIError(500, "Internal Server Error")
    }

    res.send({ message: "Log Out successful!" })
})


