const { json } = require('body-parser');
const Joi = require('joi');
const { JobInstance } = require('twilio/lib/rest/bulkexports/v1/export/job');
const Chat = require('../models/chatModel');
const User = require('../models/userModel');
const APIError = require('../utils/APIError');
const catchAsync = require('../utils/catchAsync')

module.exports.accessChat = catchAsync(async (req, res, next) => {
    const { userId } = req.query
    const { id } = req.user;
    if (!userId) throw new APIError(400, "User id required!")

    let isChat = await Chat.find({
        isGroupChat: false,
        $and: [
            { users: { $elemMatch: { $eq: id } } },
            { users: { $elemMatch: { $eq: userId } } }
        ]
    }).populate('users').populate('latestMessage');

    isChat = await User.populate(isChat, {
        path: 'latestMessage.sender',
        select: 'username avatar phone'
    })

    if (isChat.length > 0) {
        res.send({ ...isChat[0]._doc })
    }
    else {

        const user = await User.findById(userId);
        if (!user) throw new APIError(400, "User does not exist");
        console.log(user)

        const chatData = {
            chatName: "sender",
            isGroupChat: false,
            users: [id, userId],
            chatImage: user.avatar
        }

        const createdChat = await Chat.create(chatData)

        const fullChat = await Chat.findOne({ _id: createdChat._id }).populate('users')

        res.send({ ...(fullChat._doc) })

    }



});

module.exports.fetchChats = catchAsync(async (req, res, next) => {
    console.log(req.user)
    let chats = await Chat.find({
        users: { $elemMatch: { $eq: req.user.id } }
    }).populate('users').populate('groupAdmin').sort({ updatedAt: -1 })

    if (!chats) throw new APIError(400, "Chats not found!")

    res.send({ chats })
});

module.exports.createGroup = catchAsync(async (req, res, next) => {
    const schema = Joi.object({
        users: Joi.array().required().min(2),
        groupName: Joi.string().required().min(5).max(30)
    })

    const { users, groupName } = req.body;
    await schema.validateAsync({ users, groupName })

    let newGroupChat = await Chat.create({
        isGroupChat: true,
        users: [req.user.id, ...users],
        chatName: groupName,
        groupAdmin: req.user.id
    })

    newGroupChat = await (await newGroupChat.populate('users')).populate('groupAdmin')

    res.send({ groupChat: newGroupChat })
});

module.exports.renameGroup = catchAsync(async (req, res, next) => {
    const { chatID, newChatName } = req.body;
    const schema = Joi.object({
        chatID: Joi.string().required().alphanum().hex(),
        newChatName: Joi.string().required().min(5).max(30)
    })
    await schema.validateAsync({ chatID, newChatName })

    const updatedGroupChat = await Chat.findByIdAndUpdate(chatID, {
        chatName: newChatName
    }, { new: true }).populate('users').populate('groupAdmin')

    if (!updatedGroupChat) throw new APIError(400, "Chat not found!")

    res.send({ groupChat: updatedGroupChat })
});

module.exports.removeFromGroup = catchAsync(async (req, res, next) => {
    const { chatId, userId } = req.body
    const schema = Joi.object({
        chatId: Joi.string().alphanum().hex().required(),
        userId: Joi.string().alphanum().hex().required()
    })
    await schema.validateAsync({ chatId, userId })

    const group = await Chat.findById(chatId);

    if (!group) throw new APIError(400, "Chat not found!")

    if (req.user.id !== group.groupAdmin._id.toString()) throw new APIError(403, "You need to admin to remove users")


    const updatedGroup = await Chat.findByIdAndUpdate(chatId, {
        $pull: {
            users: userId
        }
    }, { new: true })



    res.send({ groupChat: updatedGroup })
});

module.exports.addToGroup = catchAsync(async (req, res, next) => {
    const { chatId, userId } = req.body
    const schema = Joi.object({
        chatId: Joi.string().alphanum().hex().required(),
        userId: Joi.string().alphanum().hex().required()
    })
    await schema.validateAsync({ chatId, userId })

    const updatedGroup = await Chat.findByIdAndUpdate(chatId, {
        $push: {
            users: userId
        }
    }, { new: true })

    if (!updatedGroup) throw new APIError(400, "Chat not found!")

    res.send({ groupChat: updatedGroup })
});
