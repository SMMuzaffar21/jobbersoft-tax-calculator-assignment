package com.jobbersoft.tax.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class JurisdictionResponse {

    private String code;
    private String name;
    private BigDecimal baseRate;
    private LocalDate effectiveDate;
}