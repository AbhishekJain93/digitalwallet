package com.hubpay.digitalwallet.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hubpay.digitalwallet.models.Transaction;
import com.hubpay.digitalwallet.models.request.CreditRequest;
import com.hubpay.digitalwallet.services.TransactionService;

import io.micrometer.common.lang.NonNull;
import jakarta.validation.Valid;

@RestController
public class DigitalWalletController {

    @Autowired
    TransactionService transactionService;

    @PostMapping("/wallets/{id}/credits")
    public String credit(@NonNull @PathVariable String id, @RequestBody @Valid CreditRequest request) {
        return String.format(
                "crediting amount in  wallet: %s, amount: %s, currency: %s, transactionRef: %s!", id,
                request.getAmount(), request.getCurrencyCode(), request.getTransactionRef());
    }

    @GetMapping("/wallets/{id}/transactions")
    public ResponseEntity<List<Transaction>> listTransactions(@NonNull @PathVariable int id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size) {
        List<Transaction> transactions = transactionService.listTransactions(100, page, size);
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

}
