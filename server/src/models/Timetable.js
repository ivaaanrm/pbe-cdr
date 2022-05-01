const mongoose = require('mongoose')

const timetableSchema = new mongoose.Schema({
  userid: {
    type: String,
    required: true,
    unique: true
  },
  day: {
    type: Number
  },
  hour: {
    type: String
  },
  subject: {
    type: String
  },
  room: {
    type: String
  }
},
{
  versionKey: false,
  timestamps: true,
})

module.exports = mongoose.model('Timetable', timetableSchema)