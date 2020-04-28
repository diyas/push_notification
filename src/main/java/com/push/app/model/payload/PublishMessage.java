package com.push.app.model.payload;

import lombok.Data;

@Data
public class PublishMessage {
    private String topic;
    private String message;
}
