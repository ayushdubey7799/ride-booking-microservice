const express = require("express");
const http = require("http");
const socketIo = require("socket.io");
const kafkaConsumer = require("./kafka/consumer");
const driverSocketHandler = require("./sockets/handler");

const app = express();
const server = http.createServer(app);
const io = socketIo(server, {
  cors: { origin: "*" }
});

// Maintain driver sockets
global.driverSockets = new Map();

io.on("connection", (socket) => {
  socket.on("register-driver", (driverId) => {
    global.driverSockets.set(driverId.toString(), socket);
    console.log(`Driver ${driverId} connected`);
  });

  socket.on("disconnect", () => {
    for (let [id, s] of global.driverSockets.entries()) {
      if (s === socket) global.driverSockets.delete(id);
    }
  });
});

setTimeout(() => {
    kafkaConsumer.startRideRequestListener(io);
    console.log("🚀 Kafka consumer started after 30s delay");
  }, 60000);

const PORT = process.env.PORT || 3001;
server.listen(PORT, () => console.log(`Realtime Gateway running on ${PORT}`));
