const Timetable = require("../models/Timetable");

const days = {
  1: "Mon",
  2: "Tue",
  3: "Wed",
  4: "Thu",
  5: "Fri",
};

function getKeyByValue(object, value) {
  return Object.keys(object).find((key) => object[key] === value);
}

const hourFormat = hour => (hour.split(":").map(part => part.padStart(2,0))).join(':')

const findTimetable = async (req, res) => {
  const query = req.query;
  query.userid = req.params.id;
  const limit = "limit" in query ? parseInt(Object.values(query.limit)[0]) : 0;
//   console.log(query.hour);

  if ("hour" in query && "day" in query) {
    const restriction = `$${Object.keys(query.hour)[0]}`;
    query.hour = { [restriction]: hourFormat(Object.values(query.hour)[0]) }; // Transform { gt: '9:00' } to { '$gt': '09:00' }
    // console.log("------", query.hour);
    query.day = parseInt(getKeyByValue(days, query.day));
    var timetableInfo = await Timetable.find(query).limit(1);

  } else if ("day" in query) {
    query.day = parseInt(getKeyByValue(days, query.day));
    var timetableInfo = await Timetable.find(query)
      .sort({ day: 1, hour: 1 })
      .limit(limit);

  } else {
    const now = new Date();
    const day = now.getDay();
    const hh = now.getHours().toString().padStart(2, 0);
    const mm = now.getMinutes().toString().padStart(2, 0);
    const ss = now.getSeconds().toString().padStart(2, 0);
    const hour = hh + ":" + mm + ":" + ss;

    query.day = { $gte: day };
    const timetable1 = await Timetable.find(query).sort({ day: 1, hour: 1 });

    query.day = { $lt: day };
    const timetable2 = await Timetable.find(query).sort({ day: 1, hour: 1 });
    var timetableInfo = timetable1.concat(timetable2);

    query.day = { $eq: day };
    const elementsToday = await Timetable.find(query).count();
    for (var i = 0; i < elementsToday; i++) {
      if (timetableInfo[i].hour < hour) {
        var e = timetableInfo.shift();
        timetableInfo.push(e);
      }
    }
    timetableInfo = limit == 0 ? timetableInfo : timetableInfo.slice(0, limit);
  }

  const timetables = timetableInfo.map((t) => {
    const timetable = {
      day: days[t.day],
      hour: t.hour,
      subject: t.subject,
      room: t.room,
    };
    return timetable;
  });
  res.json(timetables);
};

const createTimetable = async (req, res) => {
  const newTimetable = new Timetable({
    userid: req.body.userid,
    day: req.body.day,
    hour: req.body.hour,
    subject: req.body.subject,
    room: req.body.room,
  });
  const timetableSaved = await newTimetable.save();
  console.log("[DB] new Timetable saved");
  res.json(timetableSaved);
};

module.exports = { findTimetable, createTimetable };
