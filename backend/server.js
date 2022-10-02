const inProduction = process.env.NODE_ENV == "production" ? true : false;

if (!inProduction) require("dotenv").config();

const express = require('express')
const bodyParser = require('body-parser')
const crypto = require('crypto');
const jwt = require('jsonwebtoken')
const redisClient = require('./utils/initRedis')

redisClient.SET("foo", "bar")

redisClient.GET("foo")

const ErrorHandler = require("./utils/ErrorHandler");
const APIError = require("./utils/APIError");

const app = express();
const client = require('twilio')(process.env.TWILIO_ACCOUNT_SID, process.env.TWILIO_AUTH_TOKEN);

app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json())


app.post("/sendOTP", (req, res) => {
    const phone = req.body.phone;
    if (phone == null || phone == undefined) throw new APIError(400, "Missing Fields!")
    const otp = Math.floor(100000 + Math.random() * 900000);
    const ttl = 2 * 60 * 1000
    const expires = Date.now() + ttl;
    const hash = crypto.createHmac('sha256', process.env.OTP_HASH_SECRET).update(`${phone}.${otp}.${expires}`).digest('hex');
    const fullHash = `${hash}.${expires}`;

    // client.messages.create({
    //     body: "Your One Time Password(OTP) for Chat App is " + otp,
    //     from: "+17792092313",
    //     to: phone
    // }).then((messageInstance) => console.log(messageInstance)).catch((err) => console.error(err))

    res.send({ phone, hash: fullHash, otp })
})

app.post("/verifyOTP", (req, res) => {
    const phone = req.body.phone
    const hash = req.body.hash
    const otp = req.body.otp

    if (phone == null || hash == null || otp == null) throw new APIError(400, "Missing Fields")
    if (typeof hash != 'string') throw new APIError(400, "Hash should be a string")

    const [hashValue, expires] = hash.split('.');

    if (Date.now() > parseInt(expires)) {
        throw new APIError(504, "Timeout! Please try again")
    }

    const calculatedHash = crypto.createHmac('sha256', process.env.OTP_HASH_SECRET).update(`${phone}.${otp}.${expires}`).digest('hex');

    if (calculatedHash != hashValue) {
        throw new APIError(401, "Verification failed! Incorrect OTP");
    }

    const accessToken = generateNewAccessToken({ phone })
    const refreshToken = generateNewRefreshToken({ phone })
    res.send({ message: "Verification Successful", accessToken, refreshToken })
});

app.post("/refresh-token", (req, res, next) => {
    const refreshToken = req.body.refreshToken
    if (refreshToken == undefined) throw new APIError(400, "Refresh token required!")

    jwt.verify(refreshToken, process.env.JWT_REFRESH_TOKEN_SECRET, (err, user) => {
        if (err) {
            next(new APIError(403, "Refresh token invalid!"))
        }
        const accessToken = generateNewAccessToken({ phone: user.phone })
        const refreshToken = generateNewRefreshToken({ phone: user.phone })
        res.send({ accessToken, refreshToken });
    })

})

function generateNewAccessToken(user) {
    return jwt.sign(user, process.env.JWT_ACCESS_TOKEN_SECRET, { expiresIn: '1d' });
}

function generateNewRefreshToken(user) {
    return jwt.sign(user, process.env.JWT_REFRESH_TOKEN_SECRET, { expiresIn: '1y' });
}

function authenticateUser(req, res, next) {
    const authHeader = req.headers['authorization']
    const token = authHeader && authHeader.split(' ')[1]

    if (token == undefined) throw new APIError(403, "User authentication failed!")
    jwt.verify(token, process.env.JWT_ACCESS_TOKEN_SECRET, (err, user) => {
        if (err) {
            if (err.message == "TokenExpiredError") {
                next(new APIError(403, "Token Expired!"));
            }
            else {
                next(new APIError(403, "User authentication failed!"));
            }
        }
        req.user = user
        next()
    })
}

app.use(ErrorHandler)




const PORT = process.env.PORT || 5000;
app.listen(PORT, console.log(`Listening on PORT: ${PORT}`));