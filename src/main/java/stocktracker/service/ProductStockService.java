package stocktracker.service;

import stocktracker.model.dto.response.StockResponse;
import stocktracker.model.entity.ProductStock;
import stocktracker.model.enums.Unit;

import java.math.BigDecimal;
import java.util.List;

public interface ProductStockService {
    List<StockResponse> getAll();
    ProductStock getByProductId(Long productId);
    ProductStock getOrCreateStock(Long productId);
    void increaseStock(Long productId, BigDecimal quantity, BigDecimal price, Unit unit, Integer boxCount);
    void decreaseStock(Long productId, BigDecimal quantity, BigDecimal price, Unit unit, Integer boxCount);
    StockResponse getStock(Long productId);
}
