const app = require("./server"); // Importamos el server.js
async function main() {
  await app.listen(app.get("port")); // Iniciamos el server en el puerto 3000
  console.log("Listening on port", app.get("port"));
}

main();
