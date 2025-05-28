package com.mohit.chat.service.respository;

import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.util.HashMap;
import java.util.Map;

@Repository
public class RoomRepository {
    private final DynamoDbClient dynamoDbClient;
    private final String tableName = "chat-rooms";

    public RoomRepository(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    public void createRoom(String roomId) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("roomId", AttributeValue.builder().s(roomId).build());
        item.put("createdAt", AttributeValue.builder().n(String.valueOf(System.currentTimeMillis())).build());

        dynamoDbClient.putItem(PutItemRequest.builder()
                .tableName(tableName)
                .item(item)
                .build());
    }

    public boolean roomExists(String roomId) {
        GetItemResponse response = dynamoDbClient.getItem(GetItemRequest.builder()
                .tableName(tableName)
                .key(Map.of("roomId", AttributeValue.builder().s(roomId).build()))
                .build());
        return response.hasItem();
    }
}
