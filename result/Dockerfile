FROM node:20-alpine
WORKDIR /app

COPY package.json .
RUN npm install

COPY server.js .
COPY views ./views
EXPOSE 80

CMD ["node", "server.js"]