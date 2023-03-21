package com.hubpay.digitalwallet.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hubpay.digitalwallet.models.EmiratesTransaction;

public interface EmiratesTransactionRepostory extends JpaRepository<EmiratesTransaction, String> {

}
