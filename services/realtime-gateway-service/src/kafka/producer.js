const { Kafka } = require("kafkajs");

const kafka = new Kafka({ brokers: [process.env.KAFKA_BROKERS] });
const producer = kafka.producer();

let isConnected = false;

async function sendMatchResult(message) {
  if (!isConnected) {
    await producer.connect();
    isConnected = true;
  }

  console.log("✅ Sending Kafka message:", message);
  await producer.send({
    topic: "ride-matching-result-topic",
    messages: [{ value: JSON.stringify(message) }]
  });
}

module.exports = { sendMatchResult };
