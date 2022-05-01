const mongoose = require('mongoose')

const userSchema = new mongoose.Schema({
  id: {
    type: String,
    required: true,
    unique: true
  },
  username: {
    type: String
  }
},
{
  versionKey: false,
  timestamps: true,
})

module.exports = mongoose.model('User', userSchema)