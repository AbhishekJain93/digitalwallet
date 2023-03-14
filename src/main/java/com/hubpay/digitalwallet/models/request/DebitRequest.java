package com.hubpay.digitalwallet.models.request;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import com.hubpay.digitalwallet.models.request.validators.ValidCurrencyCode;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
@Valid
public class DebitRequest {
    @NotBlank(message = "transactionRef can't be empty")
    @Length(max = 36, message = "transactionRef can't be more than 36 chars")
    private String transactionRef;
    @Range(min = 1, max = 500000, message = "Amount should be between 1 and 500000")
    private long amount;
    @ValidCurrencyCode
    private String currencyCode;
}
