package com.push.app.model.payload;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;

@Data
public class RequestPayment {
    @ApiModelProperty(
            notes = "1 = Credit/Debit, " +
                    "2 = DANA, " +
                    "3 = OVO, " +
                    "4 = GOPAY, " +
                    "5 = LINKAJA, " +
                    "6 = CASH, " +
                    "7 = SPLIT"
    )
    private int trMethod;
    @ApiModelProperty(notes = "Base Amount")
    private Double trAmount;
}
