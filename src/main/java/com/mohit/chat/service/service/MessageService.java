package com.mohit.chat.service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mohit.chat.service.model.ChatMessage;
import io.lettuce.core.cluster.api.sync.RedisAdvancedClusterCommands;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class MessageService {

    private final RedisAdvancedClusterCommands<String, String> redisCommands;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final DynamoDbClient dynamoDbClient;

    public List<ChatMessage> getRecentMessages(String roomId) {
        try {
            String key = "chat:room:" + roomId;
            List<String> items = redisCommands.lrange(key, 0, -1);

            List<ChatMessage> messages = new LinkedList<>();
            for (String item : items) {
                messages.add(objectMapper.readValue(item, ChatMessage.class));
            }
            if (!messages.isEmpty()) {
                log.info("Retrieved messages from Redis cache.");
                return messages;
            }
            log.info("Cache miss. Fetching from DynamoDB.");
            return fetchMessagesFromDynamoDB(roomId);

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch messages.", e);
        }
    }

    private List<ChatMessage> fetchMessagesFromDynamoDB(String roomId) {
        List<ChatMessage> messages = new ArrayList<>();
        try {
            QueryRequest queryRequest = QueryRequest.builder()
                    .tableName("chat-messages")
                    .keyConditionExpression("roomId = :roomId")
                    .expressionAttributeValues(Map.of(":roomId", AttributeValue.builder().s(roomId).build()))
                    .build();

            QueryResponse response = dynamoDbClient.query(queryRequest);

            for (Map<String, AttributeValue> item : response.items()) {
                ChatMessage message = new ChatMessage();
                message.setRoomId(item.get("roomId").s());
                message.setSender(item.get("sender").s());
                message.setContent(item.get("content").s());
                message.setTimestamp(Long.parseLong(item.get("timestamp").n()));
                messages.add(message);
            }
        } catch (Exception e) {
            log.error("Error retrieving messages from DynamoDB: {}", e.getMessage());
        }
        return messages;
    }
}
