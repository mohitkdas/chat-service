package com.mohit.chat.service.service;

import com.mohit.chat.service.model.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class MessageService {

    @Autowired
    private RedisTemplate<String, ChatMessage> redisTemplate;

    @Autowired
    private DynamoDbClient dynamoDbClient;

    public List<ChatMessage> getRecentMessages(String roomId) {
        String key = "chat:room:" + roomId;
        List<ChatMessage> messages = redisTemplate.opsForList().range(key, 0, -1);

        if (messages != null && !messages.isEmpty()) {
            log.info("Retrieved messages from Redis cache.");
            return messages;
        }

        log.info("Cache miss. Fetching from DynamoDB.");
        return fetchMessagesFromDynamoDB(roomId);
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
