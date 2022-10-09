const APIError = require("../utils/APIError");
const jwt = require('jsonwebtoken')

module.exports = function authenticateUser(req, res, next) {
    const authHeader = req.header('authorization')
    if (authHeader == undefined) throw new APIError(403, "Authorization header required")

    const token = authHeader.split(' ')[1];
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