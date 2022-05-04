const mongoose = require("mongoose");
const taskSchema = new mongoose.Schema(
  {
    userid: {
      type: String,
      required: true,
    },
    date: Date,
    subject: String,
    name: String,
  },
  {
    versionKey: false,
    timestamps: true,
  }
);

module.exports = mongoose.model("Task", taskSchema);
