const mongoose = require('mongoose')

const timetableSchema = new mongoose.Schema({
  userid: {
    type: String,
    required: true,
  },
  day: Number,
  hour: String,
  subject: String,
  room: String
},
{
  versionKey: false,
  timestamps: true,
})

module.exports = mongoose.model('Timetable', timetableSchema)