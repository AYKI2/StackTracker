package stocktracker.model.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record StockMovementResponse(
        Long id,
        ProductResponse product,
        String type,
        BigDecimal quantity,
        BigDecimal pricePerUnit,
        BigDecimal totalPrice,
        String description,
        LocalDateTime createdAt
) {
}
