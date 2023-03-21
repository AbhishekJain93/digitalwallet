package com.hubpay.digitalwallet.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hubpay.digitalwallet.models.EmiratesTransaction;
import com.hubpay.digitalwallet.models.EmiratesTransactionEnum;
import com.hubpay.digitalwallet.repositories.EmiratesTransactionRepostory;

@Service
public class EmiratesTransactionService {

    @Autowired
    EmiratesTransactionRepostory transactionRepo;

    public void processTransactionEntry(String[] transactionFields) {
        validateTransactionEntry(transactionFields);
        EmiratesTransaction transaction = new EmiratesTransaction();
        int reservationId = Integer.parseInt(transactionFields[0]);
        String passengerName = transactionFields[1];
        String bookingStatus = transactionFields[2];

        transaction.setReservationID(reservationId);
        transaction.setPassengerName(passengerName);
        transaction.setBookingStatus(bookingStatus);

        if (bookingStatus.equals(EmiratesTransactionEnum.CONFIRMED.toString())) {
            transactionRepo.save(transaction);
        } else { // only payment-failed
            notifyCustomerAssistanceQueue(transaction);
        }
    }

    private void notifyCustomerAssistanceQueue(EmiratesTransaction transaction) {
    }

    private void validateTransactionEntry(String[] tempArr) {
        // this methods throws exception if the csv entry is not valid
    }

    // this method will handle errors for entry
    public void handleErrorForEntry(EmiratesTransaction transaction) {
        this.notifyCustomerAssistanceQueue(transaction);
    }
}
