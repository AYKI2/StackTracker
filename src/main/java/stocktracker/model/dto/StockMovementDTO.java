package stocktracker.model.dto;

import java.math.BigDecimal;

public record StockMovementDTO(
        Long productId,
        String type,
        BigDecimal quantity,
        BigDecimal pricePerUnit,
        String description
) {
}
