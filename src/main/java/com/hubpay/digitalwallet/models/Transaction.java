package com.hubpay.digitalwallet.models;

import java.util.UUID;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@DynamicInsert
@Table(name = "transactions")
public class Transaction {

    @Id
    private UUID id;

    @Column(name = "transaction_ref")
    private String transactionRef;

    @Column(name = "amount")
    private int amount;

    @Column(name = "currency_code")
    private String currencyCode;

    @Column(name = "wallet_id")
    private int walletId;

    @Column(name = "transaction_type")
    private TransactionType transactionType;

    @Generated(GenerationTime.INSERT)
    @Column(name = "created_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private java.sql.Timestamp createdAt;

    public Transaction(UUID id, String transactionRef, int amount, String currencyCode, int walletId,
            TransactionType transactionType) {
        this.id = id;
        this.transactionRef = transactionRef;
        this.amount = amount;
        this.currencyCode = currencyCode;
        this.walletId = walletId;
        this.transactionType = transactionType;
    }

}