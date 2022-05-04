require('dotenv').config();
const express = require("express"); // Import Express.js framework
const mongoose = require("mongoose"); // Import mongoose to connect to mongoDB database
const cors = require("cors")

// App Settings
const app = express();
app.set("port", process.env.PORT || 3000); // set port with environment variable in .env file
app.use(cors())
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// Database connection
const DB_URL = process.env.MONGODB_URL;
mongoose.connect(DB_URL, {
  useUnifiedTopology: true,
  useNewUrlParser: true,
});
const db = mongoose.connection;
db.once('open', _ => {
  console.log('Database connected:', db.name)
})
db.on('error', err => {
  console.error('connection error:', err)
})

// Routes
app.use(require("./routes/tasks.routes"));
app.use(require("./routes/marks.routes"));
app.use(require("./routes/timetables.routes"));
app.use(require("./routes/users.routes"));

module.exports = app

