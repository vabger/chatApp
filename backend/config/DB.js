const mongoose = require('mongoose')

const connectDB = async () => {
    try {
        await mongoose.connect(process.env.LOCAL_MONGO_URI, {
            useNewUrlParser: true,
            useUnifiedTopology: true
        });
        console.log("Database connected......")
    }
    catch (err) {
        console.error(err)
        process.exit()
    }

}

module.exports = connectDB;