package stocktracker.model.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record StockMovementDTO(
        Long productId,
        String type,
        BigDecimal pricePerUnit,
        String description,
        @NotNull(message = "Количество коробок должно быть указано!")
        Integer boxCount,
        Integer unitsPerBox
) {
}
