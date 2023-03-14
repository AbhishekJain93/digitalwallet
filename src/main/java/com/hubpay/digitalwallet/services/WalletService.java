package com.hubpay.digitalwallet.services;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hubpay.digitalwallet.exception.ServiceException;
import com.hubpay.digitalwallet.models.Transaction;
import com.hubpay.digitalwallet.models.TransactionType;
import com.hubpay.digitalwallet.models.Wallet;
import com.hubpay.digitalwallet.models.response.TransactionResponse;
import com.hubpay.digitalwallet.repositories.TransactionRepository;
import com.hubpay.digitalwallet.repositories.WalletRepository;

@Service
public class WalletService {
        @Autowired
        TransactionRepository transactionRepository;
        @Autowired
        WalletRepository walletRepository;

        Set<String> idempotentRequestSet = new HashSet<>();

        @Transactional
        public TransactionResponse credit(int walletId, long amount, String currency, String transactionRef) {
                return executeTransaction(walletId, amount, currency, transactionRef, TransactionType.CREDIT);
        }

        @Transactional
        public TransactionResponse debit(int walletId, long amount, String currency, String transactionRef) {
                return executeTransaction(walletId, amount, currency, transactionRef, TransactionType.DEBIT);
        }

        private TransactionResponse executeTransaction(int walletId, long amount, String currency,
                        String transactionRef, TransactionType type) {
                try {
                        if (!transactionRepository.findWithLockingByTransactionRef(transactionRef).isEmpty()) {
                                throw new ServiceException(
                                                String.format("transaction with ref: %s already exists",
                                                                transactionRef),
                                                HttpStatus.BAD_REQUEST);
                        }

                        return type == TransactionType.CREDIT
                                        ? executeCredit(walletId, amount, currency, transactionRef)
                                        : executeDebit(walletId, amount, currency, transactionRef);
                } catch (DataIntegrityViolationException e) {
                        throw new ServiceException(
                                        String.format("transaction already exists for transactionRef: %s",
                                                        transactionRef),
                                        HttpStatus.NOT_FOUND, e);
                }
        }

        private TransactionResponse executeDebit(int walletId, long amount, String currency, String transactionRef) {
                Wallet wallet = walletRepository.findWithLockingById(walletId).orElseThrow(() -> new ServiceException(
                                String.format("wallet with id: %d not found for transactionRef: %s",
                                                walletId,
                                                transactionRef),
                                HttpStatus.NOT_FOUND));

                validateCurrency(wallet, currency);
                validateWalletBalance(wallet, amount);

                wallet.setBalance(wallet.getBalance() - amount);
                Wallet updatedWallet = walletRepository.saveAndFlush(wallet);

                Transaction debitTransaction = transactionRepository.saveAndFlush(
                                new Transaction(UUID.randomUUID(), transactionRef, amount, currency, walletId,
                                                TransactionType.DEBIT));

                return TransactionResponse.builder().transactionId(debitTransaction.getId())
                                .transactionRef(transactionRef)
                                .updatedBalance(updatedWallet.getBalance()).walletId(walletId)
                                .createdAt(debitTransaction.getCreatedAt()).build();
        }

        private TransactionResponse executeCredit(int walletId, long amount, String currency, String transactionRef) {
                Wallet wallet = walletRepository.findWithLockingById(walletId).orElseThrow(() -> new ServiceException(
                                String.format("wallet with id: %d not found for transactionRef: %s",
                                                walletId,
                                                transactionRef),
                                HttpStatus.NOT_FOUND));

                validateCurrency(wallet, currency);

                wallet.setBalance(wallet.getBalance() + amount);
                Wallet updatedWallet = walletRepository.saveAndFlush(wallet);

                Transaction creditTransaction = transactionRepository.saveAndFlush(
                                new Transaction(UUID.randomUUID(), transactionRef, amount, currency, walletId,
                                                TransactionType.CREDIT));

                return TransactionResponse.builder().transactionId(creditTransaction.getId())
                                .transactionRef(transactionRef)
                                .updatedBalance(updatedWallet.getBalance()).walletId(walletId)
                                .createdAt(creditTransaction.getCreatedAt()).build();
        }

        private void validateCurrency(Wallet wallet, String transactionCurrency) {
                if (!wallet.getCurrencyCode().equals(transactionCurrency)) {
                        throw new ServiceException(
                                        String.format("wallet currency: %s don't match with transaction currency: %s",
                                                        wallet.getCurrencyCode(),
                                                        transactionCurrency),
                                        HttpStatus.BAD_REQUEST);
                }

        }

        private void validateWalletBalance(Wallet wallet, long transactionAmount) {
                if (wallet.getBalance() < transactionAmount) {
                        throw new ServiceException(
                                        String.format("insufficent funds for debit, wallet balance: %d, transaction amount: %d",
                                                        wallet.getBalance(), transactionAmount,
                                                        wallet.getCurrencyCode()),
                                        HttpStatus.BAD_REQUEST);
                }
        }
}
