package com.push.app.model.payload;

import lombok.Data;

import javax.persistence.Column;

@Data
public class RequestPayment {
    private String trNo;
    private String trMethod;
    private Long trAmount;
}
