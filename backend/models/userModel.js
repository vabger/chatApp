const mongoose = require('mongoose')

const userSchema = mongoose.Schema({
    username: {
        type: String,
        required: true,
        trim: true
    },
    phone: {
        type: String,
        required: true
    },
    avatar: {
        type: String,
        default:
            "https://icon-library.com/images/anonymous-avatar-icon/anonymous-avatar-icon-25.jpg",
    },
    publicKey: {
        type: String,
        required: true
    }

}, { timestamps: true })

const User = mongoose.model("User", userSchema)

module.exports = User;