package com.hubpay.digitalwallet.models.request;

import java.util.Arrays;
import java.util.Optional;

public enum ListTransactionFilter {
    ALL("ALL"),
    DEBIT("DEBIT"),
    CREDIT("CREDIT");

    private String type;

    ListTransactionFilter(String type) {
        this.type = type;
    }

    public String valueString() {
        return type;
    }

    // ****** Reverse Lookup ************//

    public static Optional<ListTransactionFilter> get(String type) {
        return Arrays.stream(ListTransactionFilter.values())
                .filter(env -> env.type.equals(type))
                .findFirst();
    }
}
