const { Kafka } = require("kafkajs");
const geo = require("../redis/geo");
const producer = require("./producer");

const kafka = new Kafka({ brokers: [process.env.KAFKA_BROKERS] });
console.log("✅ Kafka broker value:", process.env.KAFKA_BROKERS);

const consumer = kafka.consumer({ groupId: "realtime-gateway" });

async function startRideRequestListener(io) {
try{
  await consumer.connect();
  await consumer.subscribe({ topic: "ride-matching-topic" });

  consumer.run({
    eachMessage: async ({ message }) => {
      const booking = JSON.parse(message.value.toString());
      const { id,userId, pickupLat, pickupLng, vehicleType } = booking;
      const nearbyDrivers = await geo.getNearbyDrivers(pickupLat, pickupLng, 5);
      const candidates = nearbyDrivers.filter(d => d.vehicleType === vehicleType && d.status === "ONLINE");

      for (let driver of candidates) {
        console.log("DRIVER ID => " + driver.id.toString());
        setTimeout(() => {},5000);
        const socket = global.driverSockets.get(driver.id.toString());
        if (!socket) continue;

        const accepted = await sendOfferAndWait(socket, booking, driver.id);
        if (accepted) {
          await producer.sendMatchResult({
            status: "MATCHED",
            driverId: driver.id,
            userId,
            id
          });
          return;
        }
      }

      await producer.sendMatchResult({ status: "FAILED", userId });
    }
  });
}
catch(err){
    console.error("❌ Failed to start ride request listener:", err);
}
}

function sendOfferAndWait(socket, booking, driverId) {
  return new Promise((resolve) => {
    socket.emit("ride-offer", booking);

    const timer = setTimeout(() => resolve(false), 10000); // wait 10s max

    socket.once("ride-response", (data) => {
      if (data.accepted && data.driverId === driverId) {
        clearTimeout(timer);
        resolve(true);
      } else {
        resolve(false);
      }
    });
  });
}

module.exports = { startRideRequestListener };
