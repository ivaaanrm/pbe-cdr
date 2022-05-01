const User = require('../models/User')

const findUser = async (req, res) => {
    const userid = req.params.id
    const userInfo = await User.findOne({ id: userid })
    const user = {
        id: userInfo.id,
        username: userInfo.username
    }
    res.send(user)
}

module.exports = { findUser }