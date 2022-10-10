const path = require('path')
const express = require('express');
const { searchUser, updateAvatar } = require('../controllers/userControllers');
const authenticate = require('../middlewares/authenticate');

const multer = require('multer')
const upload = multer({
    dest: path.join(__dirname, "../uploads"),
})

const router = express.Router();

router.post("/search", authenticate, searchUser)
router.put("/avatar", authenticate, upload.single('avatar'), updateAvatar)

module.exports = router;