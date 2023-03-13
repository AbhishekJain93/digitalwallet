package com.hubpay.digitalwallet.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hubpay.digitalwallet.models.Transaction;
import com.hubpay.digitalwallet.models.request.CreditRequest;
import com.hubpay.digitalwallet.models.response.TransactionResponse;
import com.hubpay.digitalwallet.services.TransactionService;
import com.hubpay.digitalwallet.services.WalletService;

import io.micrometer.common.lang.NonNull;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/api")
@RestController
public class DigitalWalletController {
    @Autowired
    TransactionService transactionService;

    @Autowired
    WalletService walletService;

    @PostMapping("/wallets/{id}/credits")
    public ResponseEntity<TransactionResponse> credit(@NonNull @PathVariable int id,
            @RequestBody @Valid CreditRequest request) {
        log.info(String.format(
                "performing credit in  wallet: %s, amount: %s, currency: %s, transactionRef: %s", id,
                request.getAmount(), request.getCurrencyCode(), request.getTransactionRef()));

        TransactionResponse response = walletService.credit(id, request.getAmount(), request.getCurrencyCode(),
                request.getTransactionRef());

        return new ResponseEntity<TransactionResponse>(response, HttpStatus.OK);
    }

    @GetMapping("/wallets/{id}/transactions")
    public ResponseEntity<List<Transaction>> listTransactions(@NonNull @PathVariable int id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size) {
        List<Transaction> transactions = transactionService.listTransactions(id, page, size);
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

}
