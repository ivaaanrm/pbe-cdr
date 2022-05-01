const express = require('express')
const router = express.Router()

const userCtrl = require('../controllers/user.controller')

router.get('/:id', userCtrl.findUser)

module.exports = router