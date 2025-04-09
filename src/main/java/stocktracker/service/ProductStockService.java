package stocktracker.service;

import stocktracker.model.dto.response.StockResponse;
import stocktracker.model.entity.ProductStock;

import java.math.BigDecimal;
import java.util.List;

public interface ProductStockService {
    List<ProductStock> getAll();
    ProductStock getByProductId(Long productId);
    ProductStock getOrCreateStock(Long productId);
    void increaseStock(Long productId, BigDecimal quantity, BigDecimal price);
    void decreaseStock(Long productId, BigDecimal quantity);
    StockResponse getStock(Long productId);
}
