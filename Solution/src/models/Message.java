package models;

import enums.MessageLevel;

import java.time.LocalDateTime;
import java.util.UUID;

public class Message {
    private final String id;
    private final MessageLevel messageLevel;
    private final String content;
    private final LocalDateTime timestamp;
    private final String serviceName;

    public Message(MessageLevel messageLevel, String content, String serviceName) {
        this.id = UUID.randomUUID().toString();
        this.messageLevel = messageLevel;
        this.content = content;
        this.timestamp = LocalDateTime.now();
        this.serviceName = serviceName;
    }

    public String getId() {
        return id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public MessageLevel getMessageLevel() {
        return messageLevel;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
