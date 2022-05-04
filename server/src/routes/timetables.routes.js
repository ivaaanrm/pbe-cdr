const express = require('express')
const router = express.Router()
const timetableCtrl = require('../controllers/timetable.controller')

router.get('/:id/timetables', timetableCtrl.findTimetable )
router.post('/timetables', timetableCtrl.createTimetable)
module.exports = router