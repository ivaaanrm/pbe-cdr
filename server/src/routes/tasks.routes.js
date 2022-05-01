const express = require('express')
const router = express.Router()

const taskCtrl = require('../controllers/task.controller')

router.get('/:id/tasks', taskCtrl.findTasks)

router.post('/tasks', taskCtrl.createTask)

module.exports = router