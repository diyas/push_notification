package com.push.app.model.payload;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;

@Data
public class MessageParam implements Serializable {
    @SerializedName(value = "payment_method")
    private String paymentMethod;
    @SerializedName(value = "base_amount")
    private long baseAmount;
    @SerializedName(value = "qr_pay_app_name")
    private String qrAppName;

    public MessageParam(String paymentMethod, long baseAmount, String qrAppName) {
        this.paymentMethod = paymentMethod;
        this.baseAmount = baseAmount;
        this.qrAppName = qrAppName;
    }
}
