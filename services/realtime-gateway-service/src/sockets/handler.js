function setupSocketHandlers(io) {
  io.on("connection", (socket) => {
    console.log("New socket connection:", socket.id);

    socket.on("register-driver", (driverId) => {
      global.driverSockets.set(driverId.toString(), socket);
      console.log(`Driver ${driverId} registered and connected.`);
    });

    socket.on("ride-response", (data) => {
      const { driverId, accepted } = data;
      const sock = global.driverSockets.get(driverId.toString());
      if (sock) {
        sock.emit("driver-ack", { received: true });
      }

      socket.emit("ride-response", data); // Forward to ride-matching consumer if needed
    });

    socket.on("disconnect", () => {
      for (let [driverId, s] of global.driverSockets.entries()) {
        if (s === socket) {
          global.driverSockets.delete(driverId);
          console.log(`Driver ${driverId} disconnected.`);
          break;
        }
      }
    });
  });
}

module.exports = { setupSocketHandlers };
