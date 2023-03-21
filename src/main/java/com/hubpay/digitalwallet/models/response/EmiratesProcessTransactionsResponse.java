package com.hubpay.digitalwallet.models.response;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
@Data
@Builder
public class EmiratesProcessTransactionsResponse {
    private String status;
    private String error;
}
