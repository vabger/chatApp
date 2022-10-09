const express = require('express');
const { sendOTP, verifyOTP, refreshToken, logOut } = require('../controllers/authControllers');

const router = express.Router();

router.post("/sendOTP", sendOTP);

router.post("/verifyOTP", verifyOTP);

router.post("/refresh-token", refreshToken);

router.delete("/logout", logOut);


module.exports = router;