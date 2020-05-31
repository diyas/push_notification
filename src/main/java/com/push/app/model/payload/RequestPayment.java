package com.push.app.model.payload;

import lombok.Data;

import javax.persistence.Column;

@Data
public class RequestPayment {
    private String posId;
    private String trNo;
    private int trMethod;
    private Long trAmount;
}
