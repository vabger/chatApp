const redis = require('redis')

const redisClient = redis.createClient()


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
});

process.on('SIGINT', () => {
    redisClient.quit();
});

(async function () {
    await redisClient.connect();
})();

module.exports = redisClient