package com.hubpay.digitalwallet.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.hubpay.digitalwallet.exception.ServiceException;
import com.hubpay.digitalwallet.models.Transaction;
import com.hubpay.digitalwallet.models.TransactionType;
import com.hubpay.digitalwallet.models.request.ListTransactionFilter;
import com.hubpay.digitalwallet.repositories.TransactionRepository;

@Service
public class TransactionService {
    @Autowired
    TransactionRepository transactionRepository;

    public List<Transaction> listTransactions(int walletId, int page, int size, ListTransactionFilter filter) {
        Pageable paging = PageRequest.of(page, size);

        if (filter.equals(ListTransactionFilter.ALL)) {
            return transactionRepository.findByWalletId(walletId, paging);
        } else {
            TransactionType transactionType = TransactionType.get(filter.valueString())
                    .orElseThrow(() -> new ServiceException(
                            String.format("invalid filter provided: %s", filter.valueString()),
                            HttpStatus.BAD_REQUEST));

            return transactionRepository.findByWalletIdAndTransactionType(walletId, transactionType, paging);
        }
    }
}
