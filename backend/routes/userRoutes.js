const express = require('express');
const { createUser } = require('../controllers/userControllers');
const authenticate = require('../middlewares/authenticate');

const router = express.Router();

router.post("/create", authenticate, createUser)

module.exports = router;