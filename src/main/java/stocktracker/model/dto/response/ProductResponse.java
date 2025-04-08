package stocktracker.model.dto.response;

import java.time.LocalDateTime;

public record ProductResponse(
        Long id,
        String name,
        String unit,
        LocalDateTime createdAt) {
}
