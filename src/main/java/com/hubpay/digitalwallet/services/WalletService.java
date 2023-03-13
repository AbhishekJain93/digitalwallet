package com.hubpay.digitalwallet.services;

import java.util.HashSet;
import java.util.NoSuchElementException;
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

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WalletService {
        @Autowired
        TransactionRepository transactionRepository;
        @Autowired
        WalletRepository walletRepository;

        Set<String> idempotentRequestSet = new HashSet<>();

        @Transactional
        public TransactionResponse credit(int walletId, int amount, String currency, String transactionRef) {
                return executeTransaction(walletId, amount, currency, transactionRef, TransactionType.CREDIT);
        }

        @Transactional
        public TransactionResponse debit(int walletId, int amount, String currency, String transactionRef) {
                return executeTransaction(walletId, amount, currency, transactionRef, TransactionType.DEBIT);
        }

        private TransactionResponse executeTransaction(int walletId, int amount, String currency,
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
                } catch (NoSuchElementException e) {
                        log.error(String.format("wallet with id: %d not found for %s for transactionRef: %s",
                                        walletId,
                                        type,
                                        transactionRef), e);

                        throw new ServiceException(
                                        String.format("wallet with id: %d not found for %s for transactionRef: %s",
                                                        walletId,
                                                        type,
                                                        transactionRef),
                                        HttpStatus.NOT_FOUND, e);
                } catch (DataIntegrityViolationException e) {
                        log.error(String.format("transaction already exists for transactionRef: %s",
                                        transactionRef), e);

                        throw new ServiceException(
                                        String.format("transaction already exists for transactionRef: %s",
                                                        transactionRef),
                                        HttpStatus.NOT_FOUND, e);
                }
        }

        private TransactionResponse executeDebit(int walletId, int amount, String currency, String transactionRef) {
                Wallet wallet = walletRepository.findWithLockingById(walletId).get();

                validateCurrency(wallet, currency);

                wallet.setBalance(wallet.getBalance() - amount);
                Wallet updatedWallet = walletRepository.saveAndFlush(wallet);

                Transaction creditTransaction = transactionRepository.saveAndFlush(
                                new Transaction(UUID.randomUUID(), transactionRef, amount, currency, walletId,
                                                TransactionType.CREDIT));

                return TransactionResponse.builder().transactionId(creditTransaction.getId())
                                .transactionRef(transactionRef)
                                .updatedBalance(updatedWallet.getBalance()).walletId(walletId)
                                .createdAt(creditTransaction.getCreatedAt()).build();
        }

        private TransactionResponse executeCredit(int walletId, int amount, String currency, String transactionRef) {
                Wallet wallet = walletRepository.findWithLockingById(walletId).get();

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
                        log.error(String.format("wallet currency: %s don't match with transaction currency: %s",
                                        wallet.getCurrencyCode(),
                                        transactionCurrency));

                        throw new ServiceException(
                                        String.format("wallet currency: %s don't match with transaction currency: %s",
                                                        wallet.getCurrencyCode(),
                                                        transactionCurrency),
                                        HttpStatus.BAD_REQUEST);
                }

        }
}
