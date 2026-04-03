package com.jobbersoft.tax.service;

import com.jobbersoft.tax.dto.request.TaxCalculationRequest;
import com.jobbersoft.tax.dto.response.JurisdictionResponse;
import com.jobbersoft.tax.dto.response.TaxCalculationResponse;

import java.util.List;

public interface TaxCalculationService {

    TaxCalculationResponse calculateTax(TaxCalculationRequest request);

    List<TaxCalculationResponse> getTaxHistory(String shipmentId);

    List<JurisdictionResponse> getAllJurisdictions();
}