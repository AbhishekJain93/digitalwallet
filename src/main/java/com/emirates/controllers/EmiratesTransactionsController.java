package com.emirates.controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hubpay.digitalwallet.models.EmiratesTransaction;
import com.hubpay.digitalwallet.models.response.EmiratesProcessTransactionsResponse;
import com.hubpay.digitalwallet.services.EmiratesTransactionService;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.micrometer.common.lang.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class EmiratesTransactionsController {

    public static final String delimiter = ",";
    ExecutorService service = Executors.newCachedThreadPool();

    @Autowired
    EmiratesTransactionService transactionsService;

    @PostMapping("/folders/{id}/transactions")
    public ResponseEntity<List<String>> process(@NonNull @PathVariable String id) {
        final File folder = new File("/Users/abhishekjain/personal/digitalWallet/" + id);

        List<String> filenames = new ArrayList<>();
        for (final File fileEntry : folder.listFiles()) {

            if (fileEntry.getName().contains(".csv"))
                filenames.add(fileEntry.getName());

        }

        return new ResponseEntity<List<String>>(filenames, HttpStatus.OK);
    }

    // @PostMapping("/folders/{folderName}/transactions")
    public ResponseEntity<EmiratesProcessTransactionsResponse> process2(@NonNull @PathVariable String folderName) {

        try {
            File transacitonFolder = getFolderHandle(folderName);
            processFolderAsync(transacitonFolder);
        } catch (Exception e) {
            log.error("error while reading csv transactions file", e);
            return new ResponseEntity<EmiratesProcessTransactionsResponse>(
                    EmiratesProcessTransactionsResponse.builder().error(e.getMessage()).build(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<EmiratesProcessTransactionsResponse>(
                EmiratesProcessTransactionsResponse.builder().status("SUCCESS").build(), HttpStatus.OK);
    }

    private void processFolderAsync(File transacitonFolder) {
        List<String> filenames = new ArrayList<>();
        for (final File fileEntry : transacitonFolder.listFiles()) {
            service.execute(() -> {
                if (fileEntry.getName().contains(".csv"))
                    filenames.add(fileEntry.getName());
                process(fileEntry);
            });
        }
    }

    // we can add logic to fetch the folder from cloud
    private File getFolderHandle(String folderName) {
        return new File("/Users/abhishekjain/personal/digitalWallet/" + folderName);
    }

    private void process(File file) {

        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line = "";
            String[] tempArr;
            while ((line = br.readLine()) != null) { // refactor readLine
                tempArr = line.split(delimiter);

                transactionsService.processTransactionEntry(tempArr);

                System.out.println();
            }
            br.close();
        } catch (IOException e) {
            log.error("error while reading csv transactions file", e);
            EmiratesTransaction t = fetchTransactionFromError(e);
            transactionsService.handleErrorForEntry(t);
        }

    }

    private EmiratesTransaction fetchTransactionFromError(IOException e) {
        return null;
    }

}
