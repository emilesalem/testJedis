const Redis = require('ioredis')
const uuid = require('uuid')

const redisClient = new Redis()

function* generator() {
  for (let i = 0; i < 10000; i++) {
    const myId = Math.ceil(Math.random() * 10)
    const expired = myId <= 5
    yield { myId, token: { expired } }
  }
}

for (let generated of generator()) {
  const key = uuid()
  const value = JSON.stringify(generated)
  console.log(`key: ${key} value: ${value}`)
  redisClient.set(key, value)
    .then(value => value !== 'OK' ? console.error(value) : null)
}

console.log('done')