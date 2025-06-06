package com.mohit.chat.service.model;

import lombok.Data;

@Data
public class ChatMessage {
    private String sender;
    private String content;
    private String roomId;
    private long timestamp;
}