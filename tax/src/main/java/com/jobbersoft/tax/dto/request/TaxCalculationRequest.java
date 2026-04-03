package com.jobbersoft.tax.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class TaxCalculationRequest {

    @NotBlank(message = "Jurisdiction code is required")
    private String jurisdictionCode;

    @NotBlank(message = "Shipment ID is required")
    private String shipmentId;

    @NotNull(message = "Fuel quantity is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Fuel quantity must be greater than 0.0")
    private BigDecimal fuelQuantity;

    @NotNull(message = "Price per gallon is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price per gallon must be greater than 0.0")
    private BigDecimal pricePerGallon;
}