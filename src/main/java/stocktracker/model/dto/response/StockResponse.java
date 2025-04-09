package stocktracker.model.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record StockResponse(
        Long id,
        BigDecimal totalQuantity,
        BigDecimal lastPrice,
        LocalDateTime createdAt
) {
}
