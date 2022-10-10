const express = require('express')
const { accessChat, fetchChats, renameGroup, createGroup, removeFromGroup, addToGroup } = require('../controllers/chatControllers')

const router = express.Router();

router.route('/').post(accessChat).get(fetchChats);

router.post('/group', createGroup)
router.put('/group/rename', renameGroup)
router.put('/group/remove', removeFromGroup)
router.put('/group/add', addToGroup)

module.exports = router