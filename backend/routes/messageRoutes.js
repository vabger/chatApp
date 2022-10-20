
const express = require('express');

const { getMessages, sendMessage } = require('../controllers/messageControllers')
const router = express.Router();

router.route("/").post(getMessages).put(sendMessage)


module.exports = router;