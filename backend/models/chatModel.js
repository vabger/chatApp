const mongoose = require('mongoose')

const chatModel = mongoose.Schema({
    chatName: {
        type: String,
        trim: true
    },
    isGroupChat: {
        type: Boolean
    },
    users: [{
        type: mongoose.Schema.Types.ObjectId,
        ref: "User"
    }],
    latestMessage: {
        type: String
    },
    groupAdmin: {
        type: mongoose.Schema.Types.ObjectId,
        ref: "User"
    },
    chatImage: {
        type: String,
        default: "https://pic.onlinewebfonts.com/svg/img_258694.png"
    }
}, {
    timestamps: true
});

const Chat = mongoose.model("Chat", chatModel);
module.exports = Chat;