const PROXY_CONFIG = [
  {
    context: [
      "/api",
      "/quiz",
      "/stats",
      "/actuator"
    ],
    target: "http://localhost:8080",
    secure: false
  }
]

module.exports = PROXY_CONFIG;
