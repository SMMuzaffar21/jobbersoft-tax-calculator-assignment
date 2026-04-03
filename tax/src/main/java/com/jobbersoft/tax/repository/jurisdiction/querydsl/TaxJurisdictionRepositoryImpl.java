package com.jobbersoft.tax.repository.jurisdiction.querydsl;

import com.jobbersoft.tax.entity.QTaxJurisdiction;
import com.jobbersoft.tax.entity.TaxJurisdiction;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TaxJurisdictionRepositoryImpl implements TaxJurisdictionRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<TaxJurisdiction> findActiveByCode(String code, LocalDate asOfDate) {
        QTaxJurisdiction q = QTaxJurisdiction.taxJurisdiction;

        TaxJurisdiction result = queryFactory
                .selectFrom(q)
                .where(
                        q.code.equalsIgnoreCase(code),
                        q.effectiveDate.loe(asOfDate)
                )
                .orderBy(q.effectiveDate.desc())
                .fetchFirst();

        return Optional.ofNullable(result);
    }

    @Override
    public List<TaxJurisdiction> findByRateRange(BigDecimal minRate, BigDecimal maxRate) {
        QTaxJurisdiction q = QTaxJurisdiction.taxJurisdiction;

        return queryFactory
                .selectFrom(q)
                .where(
                        q.baseRate.goe(minRate),
                        q.baseRate.loe(maxRate)
                )
                .orderBy(q.baseRate.asc())
                .fetch();
    }
}