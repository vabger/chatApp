const inProduction = process.env.NODE_ENV == "production" ? true : false;

if (!inProduction) require("dotenv").config();

const express = require('express')
const bodyParser = require('body-parser')
const crypto = require('crypto');
const jwt = require('jsonwebtoken')
const cookieParser = require('cookie-parser')

const redisClient = require('./config/initRedis');
const connectDB = require('./config/DB')

const ErrorHandler = require("./utils/ErrorHandler");
const APIError = require("./utils/APIError");
const catchAsync = require("./utils/catchAsync");

const app = express();
const SMSclient = require('twilio')(process.env.TWILIO_ACCOUNT_SID, process.env.TWILIO_AUTH_TOKEN);

connectDB();

app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json())
app.use(cookieParser(process.env.COOKIE_SECRET))


app.post("/sendOTP", (req, res) => {
    const phone = req.body.phone;
    if (phone == null || phone == undefined) throw new APIError(400, "Missing Fields!")
    const otp = Math.floor(100000 + Math.random() * 900000);
    const ttl = 2 * 60 * 1000 //2 mins
    const expires = Date.now() + ttl;
    const hash = crypto.createHmac('sha256', process.env.OTP_HASH_SECRET).update(`${phone}.${otp}.${expires}`).digest('hex');
    const fullHash = `${hash}.${expires}`;

    // SMSclient.messages.create({
    //     body: "Your One Time Password(OTP) for Chat App is " + otp,
    //     from: "+17792092313",
    //     to: phone
    // }).then((messageInstance) => console.log(messageInstance)).catch((err) => console.error(err))

    console.log(otp)
    res.send({ phone, hash: fullHash })
})

app.post("/verifyOTP", catchAsync(async (req, res) => {
    const phone = req.body.phone
    const hash = req.body.hash
    const otp = req.body.otp

    if (phone === undefined || hash === undefined || otp === undefined) throw new APIError(400, "Missing Fields")
    if (typeof hash != 'string') throw new APIError(400, "Hash should be a string")

    const [hashValue, expires] = hash.split('.');

    if (Date.now() > parseInt(expires)) {
        throw new APIError(504, "Timeout! Please try again")
    }

    const calculatedHash = crypto.createHmac('sha256', process.env.OTP_HASH_SECRET).update(`${phone}.${otp}.${expires}`).digest('hex');

    if (calculatedHash !== hashValue) {
        throw new APIError(401, "Verification failed! Incorrect OTP");
    }

    const { accessToken, atExpiresIn } = generateNewAccessToken({ phone })

    const { refreshToken, rtExpiresIn } = await generateNewRefreshToken({ phone })

    res.cookie('accessToken', accessToken);
    res.cookie('refreshToken', refreshToken);
    res.cookie('rtExpiresIn', rtExpiresIn);
    res.cookie('atExpiresIn', atExpiresIn);

    res.send({ message: "Verification Successful!", accessToken, refreshToken })
}));

app.post("/refresh-token", catchAsync(async (req, res, next) => {
    const { refreshToken } = req.signedCookies;
    if (refreshToken == undefined) throw new APIError(400, "Refresh token required!")

    const user = await verifyRefreshToken(refreshToken);

    const [newAccessToken, atExpiresIn] = generateNewAccessToken({ phone: user.phone })
    const [newRefreshToken, rtExpiresIn] = await generateNewRefreshToken({ phone: user.phone })

    res.cookie('accessToken', newAccessToken);
    res.cookie('refreshToken', newRefreshToken);
    res.cookie('rtExpiresIn', rtExpiresIn);
    res.cookie('atExpiresIn', atExpiresIn);
    res.send({ newAccessToken, newRefreshToken })

}));

app.delete("/logout", catchAsync(async (req, res) => {
    const { refreshToken } = req.signedCookies;
    if (!refreshToken) throw new APIError(400, "Refresh token required!")

    const user = await verifyRefreshToken(refreshToken)

    try {
        await redisClient.DEL(user.phone)
    }
    catch (err) {
        throw new APIError(500, "Internal Server Error")
    }

    res.send({ message: "Log Out successful!" })
}))


function generateNewAccessToken(user) {
    const atExpiresIn = 60 * 60
    const accessToken = jwt.sign(user, process.env.JWT_ACCESS_TOKEN_SECRET, { expiresIn: atExpiresIn })
    return { accessToken, atExpiresIn };
}

async function generateNewRefreshToken(user) {
    const refreshToken = jwt.sign(user, process.env.JWT_REFRESH_TOKEN_SECRET, { expiresIn: '1y' });
    const EX = 365 * 24 * 60 * 60;
    try {
        await redisClient.set(user.phone, refreshToken, {
            EX
        });
    } catch (e) {
        throw new APIError(500, "Internal server error!");
    }

    const expiresIn = Date.now() + EX * 1000;
    return { refreshToken, rtExpiresIn: expiresIn };
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
        token = await redisClient.get(user.phone)
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
app.get('/auth', authenticateUser)

function authenticateUser(req, res, next) {
    const token = req.signedCookies.accessToken;
    console.log(req.signedCookies);

    if (token == undefined) throw new APIError(403, "Access token required")
    jwt.verify(token, process.env.JWT_ACCESS_TOKEN_SECRET, (err, user) => {
        if (err) {
            if (err.message == "TokenExpiredError") {
                next(new APIError(403, "Token Expired!"));
            }
            else {
                next(new APIError(403, "User authentication failed! Retry Login"));
            }
        }
        req.user = user
        next()
    })
}

app.use(ErrorHandler)




const PORT = process.env.PORT || 5000;
app.listen(PORT, console.log(`Listening on PORT: ${PORT}`));