package com.jobbersoft.tax.repository.jurisdiction;

import com.jobbersoft.tax.entity.TaxJurisdiction;
import com.jobbersoft.tax.repository.jurisdiction.querydsl.TaxJurisdictionRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaxJurisdictionRepository extends JpaRepository<TaxJurisdiction, Long>,
        TaxJurisdictionRepositoryCustom {

    List<TaxJurisdiction> findAllByOrderByCodeAsc();
}