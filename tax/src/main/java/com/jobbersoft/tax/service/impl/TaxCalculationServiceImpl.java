package com.jobbersoft.tax.service.impl;

import com.jobbersoft.tax.dto.request.TaxCalculationRequest;
import com.jobbersoft.tax.dto.response.JurisdictionResponse;
import com.jobbersoft.tax.dto.response.TaxCalculationResponse;
import com.jobbersoft.tax.entity.FuelTaxCalculation;
import com.jobbersoft.tax.entity.TaxJurisdiction;
import com.jobbersoft.tax.exception.JurisdictionNotFoundException;
import com.jobbersoft.tax.repository.calculation.FuelTaxCalculationRepository;
import com.jobbersoft.tax.repository.jurisdiction.TaxJurisdictionRepository;
import com.jobbersoft.tax.service.TaxCalculationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaxCalculationServiceImpl implements TaxCalculationService {

    private final TaxJurisdictionRepository taxJurisdictionRepository;
    private final FuelTaxCalculationRepository fuelTaxCalculationRepository;

    @Override
    @Transactional
    public TaxCalculationResponse calculateTax(TaxCalculationRequest request) {
        log.info("Calculating tax for shipment: {} in jurisdiction: {}",
                request.getShipmentId(), request.getJurisdictionCode());

        TaxJurisdiction jurisdiction = taxJurisdictionRepository
                .findActiveByCode(request.getJurisdictionCode(), LocalDate.now())
                .orElseThrow(() -> new JurisdictionNotFoundException(request.getJurisdictionCode()));

        BigDecimal calculatedTax = request.getFuelQuantity()
                .multiply(request.getPricePerGallon())
                .multiply(jurisdiction.getBaseRate())
                .setScale(4, RoundingMode.HALF_UP);

        FuelTaxCalculation calculation = FuelTaxCalculation.builder()
                .shipmentId(request.getShipmentId())
                .jurisdiction(jurisdiction)
                .fuelQuantity(request.getFuelQuantity())
                .pricePerGallon(request.getPricePerGallon())
                .calculatedTax(calculatedTax)
                .build();

        FuelTaxCalculation saved = fuelTaxCalculationRepository.save(calculation);

        log.info("Tax calculated successfully for shipment: {}. Tax amount: {}",
                request.getShipmentId(), calculatedTax);

        return mapToCalculationResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaxCalculationResponse> getTaxHistory(String shipmentId) {
        log.info("Fetching tax history for shipment: {}", shipmentId);

        if (shipmentId == null || shipmentId.isBlank()) {
            throw new IllegalArgumentException("Shipment ID cannot be blank");
        }

        List<FuelTaxCalculation> history = fuelTaxCalculationRepository
                .findByShipmentIdOrderByCalculatedOnDesc(shipmentId);

        log.info("Found {} records for shipment: {}", history.size(), shipmentId);

        return history.stream()
                .map(this::mapToCalculationResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<JurisdictionResponse> getAllJurisdictions() {
        log.info("Fetching all jurisdictions");

        return taxJurisdictionRepository.findAllByOrderByCodeAsc()
                .stream()
                .map(this::mapToJurisdictionResponse)
                .toList();
    }

    private TaxCalculationResponse mapToCalculationResponse(FuelTaxCalculation calculation) {
        return TaxCalculationResponse.builder()
                .shipmentId(calculation.getShipmentId())
                .jurisdictionCode(calculation.getJurisdiction().getCode())
                .jurisdictionName(calculation.getJurisdiction().getName())
                .fuelQuantity(calculation.getFuelQuantity())
                .pricePerGallon(calculation.getPricePerGallon())
                .taxRate(calculation.getJurisdiction().getBaseRate())
                .calculatedTax(calculation.getCalculatedTax())
                .calculatedOn(calculation.getCalculatedOn())
                .build();
    }

    private JurisdictionResponse mapToJurisdictionResponse(TaxJurisdiction jurisdiction) {
        return JurisdictionResponse.builder()
                .code(jurisdiction.getCode())
                .name(jurisdiction.getName())
                .baseRate(jurisdiction.getBaseRate())
                .effectiveDate(jurisdiction.getEffectiveDate())
                .build();
    }
}