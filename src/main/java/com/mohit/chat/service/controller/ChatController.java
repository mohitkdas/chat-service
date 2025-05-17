package com.mohit.chat.service.controller;

import com.mohit.chat.service.model.ChatMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.time.Instant;

@Controller
public class ChatController {

    @MessageMapping("/chat/{roomId}")
    @SendTo("/topic/room/{roomId}")
    public ChatMessage sendMessage(ChatMessage message) {
        message.setTimestamp(Instant.now().toEpochMilli());
        return message;
    }
}
