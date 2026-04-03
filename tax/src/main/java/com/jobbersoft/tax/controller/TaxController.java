package com.jobbersoft.tax.controller;

import com.jobbersoft.tax.dto.ApiResponse;
import com.jobbersoft.tax.dto.request.TaxCalculationRequest;
import com.jobbersoft.tax.dto.response.JurisdictionResponse;
import com.jobbersoft.tax.dto.response.TaxCalculationResponse;
import com.jobbersoft.tax.service.TaxCalculationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/tax/fuel")
@RequiredArgsConstructor
public class TaxController {

    private final TaxCalculationService taxCalculationService;

    @PostMapping("/calculate")
    public ResponseEntity<ApiResponse<TaxCalculationResponse>> calculateTax(
            @Valid @RequestBody TaxCalculationRequest request) {

        log.info("Received tax calculation request for shipment: {}", request.getShipmentId());

        TaxCalculationResponse response = taxCalculationService.calculateTax(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Tax calculated successfully"));
    }

    @GetMapping("/history/{shipmentId}")
    public ResponseEntity<ApiResponse<List<TaxCalculationResponse>>> getTaxHistory(
            @PathVariable String shipmentId) {

        log.info("Received history request for shipment: {}", shipmentId);

        List<TaxCalculationResponse> history = taxCalculationService.getTaxHistory(shipmentId);

        return ResponseEntity
                .ok(ApiResponse.success(history, "Tax history retrieved successfully"));
    }

    @GetMapping("/jurisdictions")
    public ResponseEntity<ApiResponse<List<JurisdictionResponse>>> getAllJurisdictions() {

        log.info("Received request to fetch all jurisdictions");

        List<JurisdictionResponse> jurisdictions = taxCalculationService.getAllJurisdictions();

        return ResponseEntity
                .ok(ApiResponse.success(jurisdictions, "Jurisdictions retrieved successfully"));
    }
}