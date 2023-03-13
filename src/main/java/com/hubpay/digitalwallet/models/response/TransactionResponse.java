package com.hubpay.digitalwallet.models.response;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
@Data
@Builder
public class TransactionResponse {
    private UUID transactionId;
    private String transactionRef;
    private int walletId;
    private int updatedBalance;
    private java.sql.Timestamp createdAt;

}
