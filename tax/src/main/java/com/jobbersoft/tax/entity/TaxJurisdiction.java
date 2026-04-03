package com.jobbersoft.tax.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "tax_jurisdiction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaxJurisdiction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 10)
    @NotBlank
    private String code;

    @Column(nullable = false, length = 100)
    @NotBlank
    private String name;

    @Column(nullable = false, precision = 5, scale = 4)
    @NotNull
    @DecimalMin("0.0")
    private BigDecimal baseRate;

    @Column(nullable = false)
    @NotNull
    private LocalDate effectiveDate;
}