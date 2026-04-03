package com.jobbersoft.tax.repository.calculation;

import com.jobbersoft.tax.entity.FuelTaxCalculation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FuelTaxCalculationRepository extends JpaRepository<FuelTaxCalculation, Long> {

    List<FuelTaxCalculation> findByShipmentIdOrderByCalculatedOnDesc(String shipmentId);
}