const redis = require('redis')

const redisClient = redis.createClient({
    port: 6379,
    host: "127.0.0.1"
})

redisClient.on('connect', () => {
    console.log("Client connected to redis......")
})

redisClient.on('ready', () => {
    console.log("Client is ready to use....")
})

redisClient.on('error', (err) => {
    console.log("Can't connect to redis", err)
})

redisClient.on('end', () => {
    console.log('Client disconnected from redis')
})


module.exports = redisClient