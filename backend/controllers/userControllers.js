const User = require('../models/userModel')

const APIError = require("../utils/APIError");
const catchAsync = require('../utils/catchAsync')

module.exports.createUser = catchAsync(async (req, res, next) => {
    const { username } = req.body;
    const { phone } = req.user;
    if (username == undefined) throw new APIError(400, "Username is required")

    const user = new User({
        username,
        phone
    })

    const savedUser = await user.save();

    res.send({ message: "User added successfully!", user: savedUser })

});

module.exports.allUsers = (req, res, next) => {
    const phone = req.body.phone;

    if (Array.isArray(phone)) {


    }
    else {


    }
}