package com.hubpay.digitalwallet.models;

import org.hibernate.annotations.DynamicInsert;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@DynamicInsert
@Table(name = "emiratestransactions")
public class EmiratesTransaction {
    @Id
    private int reservationID;
    private String passengerName;
    private String bookingStatus;
}
