package com.push.app.model.payload;

import com.push.app.model.PaymentMethodEnum;
import lombok.Data;

@Data
public class RequestPayment {
    private String posId;
    private String trNo;
    private PaymentMethodEnum trMethod;
    private Double trAmount;
}
