const { inProduction } = require("../config/globalVars")
const APIError = require("./APIError")

function ErrorHandler(error, req, res, next) {
    if (error instanceof APIError) {
        res.status(error.statusCode).send({ status: error.statusCode, message: error.message })
    }
    else {
        console.log(error)
        res.status(500).send({ status: 500, message: inProduction ? "Something went wrong!" : error.message })
    }
}

module.exports = ErrorHandler