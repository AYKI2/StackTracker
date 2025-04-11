package stocktracker.service;

import org.springframework.http.ResponseEntity;
import stocktracker.model.dto.StockMovementDTO;
import stocktracker.model.dto.response.StockMovementResponse;
import stocktracker.model.enums.MovementType;

import java.time.LocalDateTime;
import java.util.List;

public interface StockMovementService {
    List<StockMovementResponse> getAll();
    StockMovementResponse getById(Long id);
    StockMovementResponse create(StockMovementDTO request);
    StockMovementResponse update(Long id, StockMovementDTO request);
    ResponseEntity<String> delete(Long id);
    List<StockMovementResponse> filter(Long productId, MovementType type, LocalDateTime startDate, LocalDateTime endDate);

}
