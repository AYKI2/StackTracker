package stocktracker.model.dto;

import stocktracker.model.dto.request.CategoryRequest;

import java.math.BigDecimal;

public record ProductDTO(
        String name,
        String unit,
        BigDecimal pricePerUnit,
        Integer unitsInBox,
        CategoryRequest categoryRequest
) {}
