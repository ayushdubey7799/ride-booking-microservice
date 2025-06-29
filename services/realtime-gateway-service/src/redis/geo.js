const Redis = require("ioredis");
const redis = new Redis(process.env.REDIS_URL);

async function getNearbyDrivers(lat, lng, radiusKm) {
  const results = await redis.georadius(
    "drivers:geo",
    lng,
    lat,
    radiusKm,
    "km",
    "WITHDIST"
  );

  const drivers = await Promise.all(
    results.map(async ([key, distance]) => {
      const driverId = key.replace("driver:", "");
      const metadata = await redis.hgetall(key); // key = "driver:{id}"

      return {
        id: driverId,
        distance: parseFloat(distance),
        vehicleType: metadata.vehicleType || "UNKNOWN",
        status: metadata.status || "UNKNOWN"
      };
    })
  );

  return drivers;
}

module.exports = { getNearbyDrivers };
