const Redis = require("ioredis");
const redis = new Redis(process.env.REDIS_URL);

async function getNearbyDrivers(lat, lng, radiusKm) {
  const results = await redis.georadius("drivers:geo", lng, lat, radiusKm, "km", "WITHDIST");

  // Optionally pull metadata for each driver
  return results.map(([key, distance]) => {
    const driverId = key.replace("driver:", "");
    return {
      id: driverId,
      distance: parseFloat(distance),
      vehicleType: "SEDAN", // mock, ideally from Redis hash or service
      status: "ONLINE"
    };
  });
}

module.exports = { getNearbyDrivers };
