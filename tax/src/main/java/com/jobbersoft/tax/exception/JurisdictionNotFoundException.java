package com.jobbersoft.tax.exception;

public class JurisdictionNotFoundException extends RuntimeException {

    public JurisdictionNotFoundException(String code) {
        super("Jurisdiction not found for code: " + code);
    }
}