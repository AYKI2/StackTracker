package stocktracker.model.dto;

import java.time.LocalDateTime;

public record StockMovementFilterDTO(
        Long productId,
        String type, // "IN", "OUT" — опционально
        LocalDateTime startDate,
        LocalDateTime endDate
) {
}
