package stocktracker.model.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record StockMovementResponse(
        Long id,
        ProductResponse product,
        String type,
        BigDecimal totalQuantity,
        BigDecimal pricePerUnit,
        BigDecimal totalPrice,
        String description,
        Integer boxCount,
        Integer unitsInBox,
        LocalDateTime createdAt
) {
}
