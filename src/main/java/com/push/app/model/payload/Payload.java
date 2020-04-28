package com.push.app.model.payload;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.push.app.config.MessageType;
import com.push.app.model.data.MessageParam;
import lombok.Data;

@Data
public class Payload {
	private String serialNumber;
	@JsonSerialize
	private MessageType messageType;
	private MessageParam param;
}
