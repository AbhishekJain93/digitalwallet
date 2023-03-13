package com.hubpay.digitalwallet.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import com.hubpay.digitalwallet.models.Transaction;

import jakarta.persistence.LockModeType;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    List<Transaction> findByWalletId(int walletId, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Transaction> findWithLockingByTransactionRef(String transactionRef);

}