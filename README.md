# Chat Service

A WebSocket-based real-time messaging gateway built with Java and Spring Boot. This service handles real-time message delivery, dynamic chat room creation, and message history retrieval.

## 🔧 Features

- WebSocket messaging using STOMP over SockJS
- Dynamic room creation and routing
- REST API to retrieve chat history (Redis/DynamoDB fallback)
- Pushes chat messages to AWS SQS for async processing
- Redis caching for low-latency chat history
- DynamoDB-based persistent storage for room metadata

## 🧠 Engineering Decisions

### ✅ Why WebSocket + STOMP?
- Allows bidirectional real-time communication over a persistent TCP connection.
- STOMP simplifies message routing (`/app/chat/{roomId}`) and broadcasting (`/topic/room/{roomId}`).

### ✅ Redis for caching
- Used for storing the most recent 50 messages per room for quick access without hitting DynamoDB.
- Redis `LIST` data structure allows fast, ordered appends and pops.

### ✅ SQS for decoupling
- Avoids blocking the WebSocket thread with DB writes.
- Enables independent scaling of message consumers via Message Processor service.

### ✅ REST fallback
- Enables clients to fetch room history even if WebSocket is disrupted.
- Supports cold-start clients with preloaded messages from Redis/DynamoDB.

## ⚖️ Trade-Offs

| Decision                      | Trade-Off                                                     |
|------------------------------|---------------------------------------------------------------|
| Redis for recent history     | Limited memory-based storage, older messages not retained     |
| STOMP over HTTP polling      | Slightly heavier client-side library, but far better UX       |
| SQS decoupling               | Slight delay in message availability in history               |
| DynamoDB                     | Schemaless and fast, but less flexible than traditional RDBMS |
