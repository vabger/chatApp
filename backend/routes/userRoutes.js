const path = require('path')
const express = require('express');
const { searchUser, updateAvatar, getCurrentUser } = require('../controllers/userControllers');

const multer = require('multer')
const upload = multer({
    dest: path.join(__dirname, "../uploads"),
})

const router = express.Router();

router.post("/search", searchUser)
router.put("/avatar", upload.single('avatar'), updateAvatar)
router.get("/", getCurrentUser)

module.exports = router;