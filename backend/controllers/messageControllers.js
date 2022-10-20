const APIError = require("../utils/APIError");
const Message = require('../models/messageModel')
const User = require("../models/userModel")

const Chat = require("../models/chatModel")
const catchAsync = require("../utils/catchAsync")

module.exports.getMessages = catchAsync(async (req, res) => {
    const { chatId } = req.body;
    console.log(chatId)
    if (!chatId) {
        throw new APIError(400, "Chat ID required!");
    }

    let messages = await Message.find({ chat: chatId }).populate("sender").populate({
        path: "chat",
        populate: {
            path: "users"
        }
    });


    res.send({ messages })
})

module.exports.sendMessage = catchAsync(async (req, res) => {
    const { content, chatId } = req.body;
    if (!content || !chatId) {
        throw new APIError(400, "Missing fields")
    }

    const newMessage = new Message({
        sender: req.user.id,
        content,
        chat: chatId
    })


    const savedMessage = await newMessage.save()

    await Chat.findByIdAndUpdate(chatId, {
        latestMessage: content
    })

    const message = await Message.populate(savedMessage, [{
        path: "sender"
    }, {
        path: "chat",
        populate: "users"
    }]);



    res.send({ ...message._doc })

});
