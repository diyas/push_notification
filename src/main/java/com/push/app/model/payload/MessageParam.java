package com.push.app.model.payload;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import springfox.documentation.annotations.ApiIgnore;

import java.io.Serializable;

@Data
@ApiIgnore
public class MessageParam implements Serializable {
    @SerializedName(value = "payment_method")
    private String paymentMethod;
    @SerializedName(value = "base_amount")
    private Double baseAmount;
    @SerializedName(value = "pos_cloud_pointer")
    private String cloudPointer;

    public MessageParam(String paymentMethod, Double baseAmount, String cloudPointer) {
        this.paymentMethod = paymentMethod;
        this.baseAmount = baseAmount;
        this.cloudPointer = cloudPointer;
    }
}
