
const { inProduction } = require('./config/globalVars')

if (!inProduction) require("dotenv").config();

const express = require('express')
const bodyParser = require('body-parser')
const cors = require('cors')

const connectDB = require('./config/DB')

const ErrorHandler = require("./utils/ErrorHandler");

const authRoutes = require("./routes/authRoutes");
const userRoutes = require("./routes/userRoutes");
const chatRoutes = require('./routes/chatRoutes');
const messageRoutes = require("./routes/messageRoutes")
const authenticate = require('./middlewares/authenticate');

const User = require("./models/userModel")


connectDB();

const app = express();

app.use(cors())


app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json())



app.use("/auth", authRoutes)
app.use("/user", authenticate, userRoutes)
app.use("/chat", authenticate, chatRoutes)
app.use("/messages", authenticate, messageRoutes)


app.use(ErrorHandler)




const PORT = process.env.PORT || 5000;

const server = app.listen(PORT, console.log(`Listening on PORT: ${PORT}`));
const io = require("socket.io")(server, { cors: { origin: "*" } })


const connectionEstablished = (socket) => {
    console.log("Connected to socket.io");
    socket.on("setup", (userData) => {
        socket.join(userData._id);

        console.log(`user id: ${userData._id} setup complete`)
        socket.emit("connected");
    });

    socket.on("join chat", (room) => {
        socket.join(room);
        console.log("User Joined Room: " + room);
    });

    socket.on("get Key", (data) => {
        console.log(data);
        socket.in(data.receiverId).emit("receive key", data)
    })

    socket.on("key received", (data) => {
        socket.in(data.receiverId).emit("key received")
    })


    socket.on("new message", (newMessageRecieved) => {
        var chat = newMessageRecieved.chat;
        console.log(newMessageRecieved)

        if (!chat.users) return console.log("chat.users not defined");

        chat.users.forEach((user) => {
            console.log(user._id)
            if (user._id == newMessageRecieved.sender._id) return;

            socket.in(user._id).emit("message recieved", newMessageRecieved);
        });
    });

    socket.off("setup", () => {
        console.log("USER DISCONNECTED");
        socket.leave(userData._id);
    });
}


io.on("connection", (socket) => {
    connectionEstablished(socket)
});

