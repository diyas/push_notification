package com.push.app.model.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.codehaus.jackson.annotate.JsonIgnore;

@Data
public class Response {
    private int code;
    private String status;
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object data;
}
