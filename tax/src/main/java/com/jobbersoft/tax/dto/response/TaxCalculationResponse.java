package com.jobbersoft.tax.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class TaxCalculationResponse {

    private String shipmentId;
    private String jurisdictionCode;
    private String jurisdictionName;
    private BigDecimal fuelQuantity;
    private BigDecimal pricePerGallon;
    private BigDecimal taxRate;
    private BigDecimal calculatedTax;
    private LocalDateTime calculatedOn;
}