package com.push.app.model.domain;

import com.push.app.model.TrStatusEnum;
import com.push.app.model.TrTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "transaction_history")
public class TransactionHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column
    private String trNo;
    @Column
    private String invoiceNo;
    @Column
    private String trMethod;
    @Column
    private Long trAmount;
    @Column
    @CreationTimestamp
    private Date trDate;
    @Column
    @CreationTimestamp
    private Date trRequestDate;
    @Column
    @UpdateTimestamp
    private Date trResponseDate;
    @Column
    private String trTopicPos;
    @Column
    private String trTopicEdc;
    @Column
    @Enumerated(value = EnumType.STRING)
    private TrStatusEnum trStatus;
    @Column
    private String userId;
    @Column
    private String trNoPos;
    @Column
    @Enumerated(value = EnumType.STRING)
    @ApiModelProperty(notes = "Transaction Type")
    private TrTypeEnum trType;
}
