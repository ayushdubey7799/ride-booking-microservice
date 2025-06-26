# --- Dockerfile ---
FROM node:18-alpine

# Set working directory
WORKDIR /app

# Copy dependencies
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy app code
COPY . .

# Expose port
EXPOSE 3001

# Run the app
CMD ["node", "src/index.js"]
