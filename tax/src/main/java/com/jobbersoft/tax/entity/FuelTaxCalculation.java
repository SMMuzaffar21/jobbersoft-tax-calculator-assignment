package com.jobbersoft.tax.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "fuel_tax_calculation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FuelTaxCalculation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    @NotBlank
    private String shipmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jurisdiction_id", nullable = false)
    @NotNull
    private TaxJurisdiction jurisdiction;

    @Column(nullable = false, precision = 15, scale = 4)
    @NotNull
    @DecimalMin("0.0")
    private BigDecimal fuelQuantity;

    @Column(nullable = false, precision = 10, scale = 4)
    @NotNull
    @DecimalMin("0.0")
    private BigDecimal pricePerGallon;

    @Column(nullable = false, precision = 15, scale = 4)
    @NotNull
    private BigDecimal calculatedTax;

    @Column(nullable = false)
    private LocalDateTime calculatedOn;

    @PrePersist
    protected void onCreate() {
        this.calculatedOn = LocalDateTime.now();
    }
}