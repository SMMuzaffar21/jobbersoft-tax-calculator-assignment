package com.jobbersoft.tax.repository;

import com.jobbersoft.tax.config.QueryDslConfig;
import com.jobbersoft.tax.entity.TaxJurisdiction;
import com.jobbersoft.tax.repository.jurisdiction.TaxJurisdictionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(QueryDslConfig.class)
class TaxJurisdictionRepositoryTest {

    @Autowired
    private TaxJurisdictionRepository taxJurisdictionRepository;

    @BeforeEach
    void setUp() {
        taxJurisdictionRepository.deleteAll();

        taxJurisdictionRepository.saveAll(List.of(
                buildJurisdiction("AA", "State AA", "0.0660", "2024-01-01"),
                buildJurisdiction("BB", "State BB", "0.0200", "2024-01-01"),
                buildJurisdiction("CC", "State CC", "0.0817", "2024-01-01"),
                buildJurisdiction("DD", "State DD", "0.0363", "2024-01-01")
        ));
    }

    // ─── findActiveByCode ─────────────────────────────────────────────────────

    @Test
    void findActiveByCode_returnsLatestActiveRate() {
        Optional<TaxJurisdiction> result = taxJurisdictionRepository
                .findActiveByCode("AA", LocalDate.of(2026, 4, 3));

        assertThat(result).isPresent();
        assertThat(result.get().getCode()).isEqualTo("AA");
        assertThat(result.get().getBaseRate()).isEqualByComparingTo("0.0660");
    }

    @Test
    void findActiveByCode_returnsEmpty_whenCodeDoesNotExist() {
        Optional<TaxJurisdiction> result = taxJurisdictionRepository
                .findActiveByCode("ZZ", LocalDate.now());

        assertThat(result).isEmpty();
    }

    @Test
    void findActiveByCode_returnsEmpty_whenDateIsBeforeAllEffectiveDates() {
        Optional<TaxJurisdiction> result = taxJurisdictionRepository
                .findActiveByCode("AA", LocalDate.of(2020, 1, 1));

        assertThat(result).isEmpty();
    }

    // ─── findByRateRange ──────────────────────────────────────────────────────

    @Test
    void findByRateRange_returnsJurisdictionsWithinRange() {
        List<TaxJurisdiction> result = taxJurisdictionRepository
                .findByRateRange(
                        new BigDecimal("0.0200"),
                        new BigDecimal("0.0700")
                );

        assertThat(result).isNotEmpty();
        result.forEach(j ->
                assertThat(j.getBaseRate()).isBetween(
                        new BigDecimal("0.0200"),
                        new BigDecimal("0.0700")
                )
        );
    }

    @Test
    void findByRateRange_returnsEmpty_whenNoJurisdictionsInRange() {
        List<TaxJurisdiction> result = taxJurisdictionRepository
                .findByRateRange(
                        new BigDecimal("0.9000"),
                        new BigDecimal("0.9999")
                );

        assertThat(result).isEmpty();
    }

    @Test
    void findByRateRange_returnsOrderedByRateAscending() {
        List<TaxJurisdiction> result = taxJurisdictionRepository
                .findByRateRange(
                        new BigDecimal("0.0000"),
                        new BigDecimal("0.9999")
                );

        assertThat(result).isNotEmpty();

        // verify each rate is >= the previous one
        for (int i = 1; i < result.size(); i++) {
            assertThat(result.get(i).getBaseRate())
                    .isGreaterThanOrEqualTo(result.get(i - 1).getBaseRate());
        }
    }

    // ─── helper ───────────────────────────────────────────────────────────────

    private TaxJurisdiction buildJurisdiction(String code, String name,
                                              String rate, String date) {
        return TaxJurisdiction.builder()
                .code(code)
                .name(name)
                .baseRate(new BigDecimal(rate))
                .effectiveDate(LocalDate.parse(date))
                .build();
    }
}