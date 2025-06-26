const { Kafka } = require("kafkajs");

const kafka = new Kafka({ brokers: [process.env.KAFKA_BROKERS] });
const producer = kafka.producer();

async function sendMatchResult(message) {
  console.log("Sending message to kafka " + message);
  await producer.connect();
  await producer.send({
    topic: "ride-matching-result-topic",
    messages: [{ value: JSON.stringify(message) }]
  });
}

module.exports = { sendMatchResult };
