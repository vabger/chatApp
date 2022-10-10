const Joi = require('joi');
const fs = require('fs')
const { cloudinaryClient } = require('../config/cloudinaryClient');
const User = require('../models/userModel');
const APIError = require('../utils/APIError');

const catchAsync = require('../utils/catchAsync')



module.exports.searchUser = catchAsync(async (req, res, next) => {
    const { phone } = req.body;
    const schema = Joi.object({
        phone: Joi.array().required()
    })

    await schema.validateAsync({ phone })

    const users = await User.find({ 'phone': { $in: phone } })
    res.send({ users, size: users.length })

});

module.exports.updateUsername = catchAsync(async (req, res, next) => {
    const { username } = req.body;

    const schema = Joi.object({
        username: Joi.string().required().min(5).max(30)
    })

    schema.validateAsync({ username })

    const user = await User.findByIdAndUpdate(req.user.id, {
        username
    }, {
        new: true
    })

    res.send({ user })

})

module.exports.updateAvatar = catchAsync(async (req, res, next) => {
    const { file, user } = req;
    if (file === undefined) throw new APIError(400, "Avatar required!")

    //upload image to cloudinary
    try {
        const avatar = await cloudinaryClient.uploader.upload(file.path)
    }
    catch (err) {
        fs.unlink(file.path, (err) => {
            if (err) {
                console.log(err)
            }
        })
        throw new APIError(400, "Unable to update avatar")
    }


    //update the link in DB
    const updatedUser = await User.findByIdAndUpdate(user.id, {
        avatar: avatar.secure_url
    })

    //delete the uploaded file
    fs.unlink(file.path, (err) => {
        if (err) {
            console.log(err)
        }
    })

    res.send({ message: "Avatar updated successfully", user: updatedUser })

});



