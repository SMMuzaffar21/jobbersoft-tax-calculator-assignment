CREATE TABLE tax_jurisdiction (
    id            BIGSERIAL PRIMARY KEY,
    code          VARCHAR(10)     NOT NULL UNIQUE,
    name          VARCHAR(100)    NOT NULL,
    base_rate     DECIMAL(5, 4)   NOT NULL,
    effective_date DATE           NOT NULL
);

CREATE TABLE fuel_tax_calculation (
    id              BIGSERIAL PRIMARY KEY,
    shipment_id     VARCHAR(50)     NOT NULL,
    jurisdiction_id BIGINT          NOT NULL REFERENCES tax_jurisdiction(id),
    fuel_quantity   DECIMAL(15, 4)  NOT NULL,
    price_per_gallon DECIMAL(10, 4) NOT NULL,
    calculated_tax  DECIMAL(15, 4)  NOT NULL,
    calculated_on   TIMESTAMP       NOT NULL
);

CREATE INDEX idx_fuel_tax_calculation_shipment_id ON fuel_tax_calculation(shipment_id);
CREATE INDEX idx_tax_jurisdiction_code ON tax_jurisdiction(code);