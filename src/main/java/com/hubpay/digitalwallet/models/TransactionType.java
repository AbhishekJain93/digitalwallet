package com.hubpay.digitalwallet.models;

import java.util.Arrays;
import java.util.Optional;

public enum TransactionType {
    DEBIT("DEBIT"),
    CREDIT("CREDIT");

    private String type;

    TransactionType(String type) {
        this.type = type;
    }

    public String valueString() {
        return type;
    }

    // ****** Reverse Lookup ************//

    public static Optional<TransactionType> get(String type) {
        return Arrays.stream(TransactionType.values())
                .filter(env -> env.type.equals(type))
                .findFirst();
    }
}
