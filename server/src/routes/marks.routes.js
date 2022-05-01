const express = require('express')
const router = express.Router()

const markCtrl = require('../controllers/mark.controller')

router.get('/:id/marks', markCtrl.findMarks)

router.post('/marks', markCtrl.createMark)

module.exports = router