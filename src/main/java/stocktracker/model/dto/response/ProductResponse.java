package stocktracker.model.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductResponse(
        Long id,
        String name,
        String unit,
        BigDecimal pricePerUnit,
        BigDecimal boxPrice,
        Integer unitsInBox,
        CategoryResponse categoryResponse,
        StockResponse stockResponse,
        LocalDateTime createdAt
) {
}
