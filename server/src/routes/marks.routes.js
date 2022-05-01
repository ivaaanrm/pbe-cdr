const express = require('express')
const router = express.Router()
const markCtrl = require('../controllers/mark.controller')

router.get('/:id/marks',  markCtrl.findMarks)

module.exports = router