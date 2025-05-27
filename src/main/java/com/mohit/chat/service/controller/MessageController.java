package com.mohit.chat.service.controller;

import com.mohit.chat.service.model.ChatMessage;
import com.mohit.chat.service.service.MessageService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/{roomId}")
    public List<ChatMessage> getMessages(@PathVariable String roomId) {
        return messageService.getRecentMessages(roomId);
    }

}
