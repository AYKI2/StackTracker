package stocktracker.model.dto;

import java.math.BigDecimal;

public record ProductDTO(
        String name,
        String unit,
        BigDecimal pricePerUnit,
        BigDecimal boxPrice,
        Integer unitsInBox
) {}
