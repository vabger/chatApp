const { inProduction } = require('./config/globalVars')

if (!inProduction) require("dotenv").config();

const express = require('express')
const bodyParser = require('body-parser')

const connectDB = require('./config/DB')

const ErrorHandler = require("./utils/ErrorHandler");

const authRoutes = require("./routes/authRoutes");
const userRoutes = require("./routes/userRoutes");
const chatRoutes = require('./routes/chatRoutes')
const authenticate = require('./middlewares/authenticate');

const app = express();


connectDB();

app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json())


app.use("/auth", authRoutes)
app.use("/user", authenticate, userRoutes)
app.use("/chat", authenticate, chatRoutes)


app.use(ErrorHandler)




const PORT = process.env.PORT || 5000;
app.listen(PORT, console.log(`Listening on PORT: ${PORT}`));