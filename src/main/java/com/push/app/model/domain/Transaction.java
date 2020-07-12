package com.push.app.model.domain;

import com.push.app.model.TrStatus;
import lombok.Data;
import lombok.Value;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "transaction")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column
    private String trNo;
    @Column
    private String invoiceNo;
    @Column
    private int trMethod;
    @Column
    private Double trAmount;
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
    private TrStatus trStatus;
    @Column
    private String userId;
}
