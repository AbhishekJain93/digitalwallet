package com.hubpay.digitalwallet.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.hubpay.digitalwallet.models.Transaction;
import com.hubpay.digitalwallet.repositories.TransactionRepository;

@Service
public class TransactionService {
    @Autowired
    TransactionRepository transactionRepository;

    public List<Transaction> listTransactions(int walletId, int page, int size) {
        Pageable paging = PageRequest.of(page, size);
        return transactionRepository.findByWalletId(walletId, paging);
    }
}
