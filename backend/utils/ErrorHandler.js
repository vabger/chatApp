const APIError = require("./APIError")

function ErrorHandler(error, req, res, next) {
    if (error instanceof APIError) {
        res.status(error.statusCode).send({ status: error.statusCode, message: error.message })
    }
}

module.exports = ErrorHandler