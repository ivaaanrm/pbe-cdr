const Mark = require("../models/Mark");

const findMarks = async (req, res) => {
  const query = req.query;
  query.userid = req.params.id;
  if ("mark" in query) {
    const restriction = `$${Object.keys(query.mark)[0]}`;
    const number = parseFloat(Object.values(query.mark)[0]);
    query.mark = { [restriction]: number };
  }
  const marksInfo = await Mark.find(query).sort("subject");
  const marks = marksInfo.map((m) => {
    const mark = {
      subject: m.subject,
      name: m.name,
      mark: m.mark.toFixed(1),
    };
    return mark;
  });
  res.json(marks);
};


const createMark = async (req, res) => {
  const newMark = new Mark({
    userid: req.body.userid,
    mark: req.body.mark,
    name: req.body.name,
    subject: req.body.subject
  })
  
  const markSaved = await newMark.save();
  console.log("[DB] new Mark saved")
  res.json(markSaved);
};

module.exports = { findMarks, createMark };
