package com.hubpay.digitalwallet.models.request.validators;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidCurrencyCodeValidator implements ConstraintValidator<ValidCurrencyCode, String> {
    Logger logger = LoggerFactory.getLogger(ValidCurrencyCodeValidator.class);

    @Override
    public void initialize(ValidCurrencyCode validCurrencyCode) {
    }

    @Override
    public boolean isValid(String inputCurrency, ConstraintValidatorContext constraintValidatorContext) {
        Set<String> validCurrencies = Sets.newHashSet("GBP", "INR");
        return validCurrencies.contains(inputCurrency);
    }
}