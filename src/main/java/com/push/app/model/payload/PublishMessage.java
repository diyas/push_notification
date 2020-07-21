package com.push.app.model.payload;

import lombok.Data;
import springfox.documentation.annotations.ApiIgnore;

@Data
@ApiIgnore
public class PublishMessage {
    private String topic;
    private String message;
}
