package com.mohit.chat.service.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.mohit.chat.service.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class SqsMessageSender {
    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String queueUrl = "https://sqs.us-east-1.amazonaws.com/755431179999/chat-message-queue";

    public void sendToQueue(ChatMessage message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            SendMessageRequest sendMsgRequest = SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(json)
                    .build();
            sqsClient.sendMessage(sendMsgRequest);
            log.info("Message sent to SQS: {}", json);
        } catch (Exception e) {
            log.error("Failed to send message to SQS", e);
        }
    }
}
