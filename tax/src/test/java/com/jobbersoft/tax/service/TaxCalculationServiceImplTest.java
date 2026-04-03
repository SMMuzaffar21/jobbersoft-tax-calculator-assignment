package com.jobbersoft.tax.service;

import com.jobbersoft.tax.dto.request.TaxCalculationRequest;
import com.jobbersoft.tax.dto.response.JurisdictionResponse;
import com.jobbersoft.tax.dto.response.TaxCalculationResponse;
import com.jobbersoft.tax.entity.FuelTaxCalculation;
import com.jobbersoft.tax.entity.TaxJurisdiction;
import com.jobbersoft.tax.exception.JurisdictionNotFoundException;
import com.jobbersoft.tax.repository.calculation.FuelTaxCalculationRepository;
import com.jobbersoft.tax.repository.jurisdiction.TaxJurisdictionRepository;
import com.jobbersoft.tax.service.impl.TaxCalculationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaxCalculationServiceImplTest {

    @Mock
    private TaxJurisdictionRepository taxJurisdictionRepository;

    @Mock
    private FuelTaxCalculationRepository fuelTaxCalculationRepository;

    @InjectMocks
    private TaxCalculationServiceImpl taxCalculationService;

    private TaxJurisdiction mockJurisdiction;

    @BeforeEach
    void setUp() {
        mockJurisdiction = TaxJurisdiction.builder()
                .id(1L)
                .code("CA")
                .name("California")
                .baseRate(new BigDecimal("0.0660"))
                .effectiveDate(LocalDate.of(2024, 1, 1))
                .build();
    }

    //calculateTax
    @Test
    void calculateTax_validRequest_returnsCorrectTax() {
        // arrange
        TaxCalculationRequest request = TaxCalculationRequest.builder()
                .jurisdictionCode("CA")
                .shipmentId("SHIP-001")
                .fuelQuantity(new BigDecimal("100"))
                .pricePerGallon(new BigDecimal("3.50"))
                .build();

        FuelTaxCalculation savedCalculation = FuelTaxCalculation.builder()
                .id(1L)
                .shipmentId("SHIP-001")
                .jurisdiction(mockJurisdiction)
                .fuelQuantity(new BigDecimal("100"))
                .pricePerGallon(new BigDecimal("3.50"))
                .calculatedTax(new BigDecimal("23.1000"))
                .calculatedOn(LocalDateTime.now())
                .build();

        when(taxJurisdictionRepository.findActiveByCode("CA", LocalDate.now()))
                .thenReturn(Optional.of(mockJurisdiction));
        when(fuelTaxCalculationRepository.save(any()))
                .thenReturn(savedCalculation);

        // act
        TaxCalculationResponse response = taxCalculationService.calculateTax(request);

        // assert
        assertThat(response.getCalculatedTax()).isEqualByComparingTo("23.1000");
        assertThat(response.getJurisdictionCode()).isEqualTo("CA");
        assertThat(response.getShipmentId()).isEqualTo("SHIP-001");
        assertThat(response.getTaxRate()).isEqualByComparingTo("0.0660");
        verify(fuelTaxCalculationRepository, times(1)).save(any());
    }

    @Test
    void calculateTax_unknownJurisdiction_throwsJurisdictionNotFoundException() {
        // arrange
        TaxCalculationRequest request = TaxCalculationRequest.builder()
                .jurisdictionCode("XYZ")
                .shipmentId("SHIP-001")
                .fuelQuantity(new BigDecimal("100"))
                .pricePerGallon(new BigDecimal("3.50"))
                .build();

        when(taxJurisdictionRepository.findActiveByCode("XYZ", LocalDate.now()))
                .thenReturn(Optional.empty());

        // act & assert
        assertThatThrownBy(() -> taxCalculationService.calculateTax(request))
                .isInstanceOf(JurisdictionNotFoundException.class)
                .hasMessageContaining("XYZ");

        verify(fuelTaxCalculationRepository, never()).save(any());
    }

    //getTaxHistory

    @Test
    void getTaxHistory_validShipmentId_returnsHistory() {
        // arrange
        FuelTaxCalculation calculation = FuelTaxCalculation.builder()
                .id(1L)
                .shipmentId("SHIP-001")
                .jurisdiction(mockJurisdiction)
                .fuelQuantity(new BigDecimal("100"))
                .pricePerGallon(new BigDecimal("3.50"))
                .calculatedTax(new BigDecimal("23.1000"))
                .calculatedOn(LocalDateTime.now())
                .build();

        when(fuelTaxCalculationRepository.findByShipmentIdOrderByCalculatedOnDesc("SHIP-001"))
                .thenReturn(List.of(calculation));

        // act
        List<TaxCalculationResponse> history = taxCalculationService.getTaxHistory("SHIP-001");

        // assert
        assertThat(history).hasSize(1);
        assertThat(history.get(0).getShipmentId()).isEqualTo("SHIP-001");
        assertThat(history.get(0).getCalculatedTax()).isEqualByComparingTo("23.1000");
    }

    @Test
    void getTaxHistory_noRecordsFound_returnsEmptyList() {
        // arrange
        when(fuelTaxCalculationRepository.findByShipmentIdOrderByCalculatedOnDesc("SHIP-999"))
                .thenReturn(List.of());

        // act
        List<TaxCalculationResponse> history = taxCalculationService.getTaxHistory("SHIP-999");

        // assert
        assertThat(history).isEmpty();
    }

    @Test
    void getTaxHistory_blankShipmentId_throwsIllegalArgumentException() {
        // act & assert
        assertThatThrownBy(() -> taxCalculationService.getTaxHistory("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Shipment ID cannot be blank");

        verify(fuelTaxCalculationRepository, never())
                .findByShipmentIdOrderByCalculatedOnDesc(any());
    }

    @Test
    void getTaxHistory_nullShipmentId_throwsIllegalArgumentException() {
        // act & assert
        assertThatThrownBy(() -> taxCalculationService.getTaxHistory(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Shipment ID cannot be blank");

        verify(fuelTaxCalculationRepository, never())
                .findByShipmentIdOrderByCalculatedOnDesc(any());
    }

    //getAllJurisdictions

    @Test
    void getAllJurisdictions_returnsAllJurisdictions() {
        // arrange
        TaxJurisdiction texas = TaxJurisdiction.builder()
                .id(2L)
                .code("TX")
                .name("Texas")
                .baseRate(new BigDecimal("0.0200"))
                .effectiveDate(LocalDate.of(2024, 1, 1))
                .build();

        when(taxJurisdictionRepository.findAllByOrderByCodeAsc())
                .thenReturn(List.of(mockJurisdiction, texas));

        // act
        List<JurisdictionResponse> jurisdictions = taxCalculationService.getAllJurisdictions();

        // assert
        assertThat(jurisdictions).hasSize(2);
        assertThat(jurisdictions.get(0).getCode()).isEqualTo("CA");
        assertThat(jurisdictions.get(1).getCode()).isEqualTo("TX");
    }

    @Test
    void getAllJurisdictions_noJurisdictions_returnsEmptyList() {
        // arrange
        when(taxJurisdictionRepository.findAllByOrderByCodeAsc())
                .thenReturn(List.of());

        // act
        List<JurisdictionResponse> jurisdictions = taxCalculationService.getAllJurisdictions();

        // assert
        assertThat(jurisdictions).isEmpty();
    }
}