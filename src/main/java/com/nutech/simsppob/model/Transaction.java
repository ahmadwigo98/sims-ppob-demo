package com.nutech.simsppob.model;

import com.nutech.simsppob.util.TransactionTypeEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.HashMap;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transaction")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String invoiceNumber;
    private String serviceCode;
    private String serviceName;
    @Enumerated(EnumType.STRING)
    private TransactionTypeEnum transactionType;
    private Integer totalAmount;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_account_id")
    private UserAccount userAccount;

    public HashMap<String, Object> toObjectData () {
        HashMap<String, Object> transactionResponseObject = new HashMap<>();

        transactionResponseObject.put("invoice_number", this.invoiceNumber);
        transactionResponseObject.put("service_code", this.serviceCode);
        transactionResponseObject.put("service_name", this.serviceName);
        transactionResponseObject.put("transaction_type", this.transactionType);
        transactionResponseObject.put("total_amount", this.totalAmount);
        transactionResponseObject.put("created_on", this.createdOn);

        return transactionResponseObject;
    }
}
