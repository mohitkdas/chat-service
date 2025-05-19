package com.mohit.chat.service.controller;

import com.mohit.chat.service.model.ChatMessage;
import com.mohit.chat.service.service.SqsMessageSender;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.time.Instant;

@Controller
public class ChatController {

    private final SqsMessageSender sqsMessageSender;

    public ChatController(SqsMessageSender sqsMessageSender) {
        this.sqsMessageSender = sqsMessageSender;
    }

    @MessageMapping("/chat/{roomId}")
    @SendTo("/topic/room/{roomId}")
    public ChatMessage sendMessage(ChatMessage message) {
        message.setTimestamp(Instant.now().toEpochMilli());
        sqsMessageSender.sendToQueue(message);
        return message;
    }
}
