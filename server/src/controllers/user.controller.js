const User = require("../models/User");

const findUser = async (req, res) => {
  const userid = req.params.id;
  const username = req.query.username
  console.log(userid + " - "+ username)
  const userInfo = await User.findOne({ id: userid });
  if (userInfo == null || userInfo.username != username) {
    const notFound = {
      id: "",
      username: "",
    };
    console.log("Error")
    res.status(404).send(notFound);
  } else {
    const user = {
      id: userInfo.id,
      username: userInfo.username,
    };
    // console.log("ok")
    res.send(user);
  }
  console.log(res.status)
};

module.exports = { findUser };
