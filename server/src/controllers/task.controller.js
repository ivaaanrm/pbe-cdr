const Task = require("../models/Task");

const findTasks = async (req, res) => {
  const query = req.query;
  query.userid = req.params.id;
  const limit = "limit" in query ? parseInt(Object.values(query.limit)[0]) : 0;
  if ("date" in query) {
    if (typeof query.date == "object") {
      const restriction = `$${Object.keys(query.date)[0]}`;
      query.date = { [restriction]: new Date() };
    } else {
      query.date = new Date(query.date);
    }
  }
  const tasksInfo = await Task.find(query).sort("date").limit(limit);
  const tasks = tasksInfo.map((t) => {
    const task = {
      date: t.date.toJSON().split("T")[0],
      subject: t.subject,
      name: t.name,
    };
    return task;
  });
  res.send(tasks);
};


const createTask = async (req, res) => {
  // console.log(req.body)
  const newTask = new Task({
    userid: req.body.userid,
    date: new Date(req.body.date),
    subject: req.body.subject,
    name: req.body.name
  })
  const taskSaved = await newTask.save();
  console.log("[DB] new Task saved")
  res.json(taskSaved);
};

module.exports = { findTasks, createTask };
