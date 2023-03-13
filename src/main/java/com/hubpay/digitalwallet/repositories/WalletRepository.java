package com.hubpay.digitalwallet.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import com.hubpay.digitalwallet.models.Wallet;

import jakarta.persistence.LockModeType;

public interface WalletRepository extends JpaRepository<Wallet, Integer> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Wallet> findWithLockingById(Integer id);

}
