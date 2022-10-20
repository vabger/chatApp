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

module.exports.getCurrentUser = catchAsync(async (req, res, next) => {
    console.log(req.user.id);

    const user = await User.findById(req.user.id)

    if (!user) throw new APIError(404, "User not found!")

    res.send({ ...user._doc });
})


module.exports.updateUsername = catchAsync(async (req, res, next) => {
    const { username } = req.query;
    if (!username) throw new APIError(400, "Bad Request");

    const user = await User.findByIdAndUpdate(req.user.id, { username }, { new: true })
    res.send({ ...user._doc })

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



