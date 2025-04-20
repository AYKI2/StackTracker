package stocktracker.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import stocktracker.exception.NotFoundException;
import stocktracker.model.dto.response.StockResponse;
import stocktracker.model.entity.Product;
import stocktracker.model.entity.ProductStock;
import stocktracker.model.entity.StockMovement;
import stocktracker.model.enums.MovementType;
import stocktracker.model.enums.Unit;
import stocktracker.repository.ProductRepository;
import stocktracker.repository.ProductStockRepository;
import stocktracker.repository.StockMovementRepository;
import stocktracker.service.ProductStockService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductStockServiceImpl implements ProductStockService {

    private final ProductStockRepository productStockRepository;
    private final ProductRepository productRepository;
    private static final Logger logger = LoggerFactory.getLogger(ProductStockServiceImpl.class);
    private final StockMovementRepository stockMovementRepository;

    public ProductStockServiceImpl(ProductStockRepository productStockRepository, ProductRepository productRepository, StockMovementRepository stockMovementRepository) {
        this.productStockRepository = productStockRepository;
        this.productRepository = productRepository;
        this.stockMovementRepository = stockMovementRepository;
    }

    @Override
    public ProductStock getOrCreateStock(Long productId) {
        return productStockRepository.findByProduct_Id(productId)
                .orElseGet(() -> {
                    Product product = productRepository.findById(productId)
                            .orElseThrow(() -> new NotFoundException("Товар не найден"));
                    ProductStock newStock = new ProductStock();
                    newStock.setProduct(product);
                    newStock.setTotalQuantity(BigDecimal.ZERO);
                    newStock.setBoxCount(0);
                    newStock.setTotalValue(BigDecimal.ZERO);
                    newStock.setLastPrice(BigDecimal.ZERO);
                    newStock.setCreatedAt(LocalDateTime.now());
                    return productStockRepository.save(newStock);
                });
    }

    @Override
    public void recalculateStock(Long productId) {
        ProductStock stock = getOrCreateStock(productId);
        List<StockMovement> movements = stockMovementRepository.findByProductIdAndDeletedFalse(productId);

        BigDecimal totalQuantity = BigDecimal.ZERO;
        BigDecimal totalValue = BigDecimal.ZERO;
        BigDecimal lastPrice = BigDecimal.ZERO;
        int boxCount = 0;

        for (StockMovement movement : movements) {
            if (movement.getType() == MovementType.IN) {
                totalQuantity = totalQuantity.add(movement.getTotalQuantity());
                BigDecimal incomingPrice = movement.getTotalPrice() != null ? movement.getTotalPrice() : BigDecimal.ZERO;
                totalValue = totalValue.add(incomingPrice);
                lastPrice = movement.getPricePerUnit(); // Последняя цена прихода
                boxCount += movement.getBoxCount();
            } else if (movement.getType() == MovementType.OUT) {
                totalQuantity = totalQuantity.subtract(movement.getTotalQuantity());
                BigDecimal outgoingPrice = movement.getPricePerUnit() != null
                        ? movement.getPricePerUnit().multiply(movement.getTotalQuantity())
                        : BigDecimal.ZERO;
                totalValue = totalValue.subtract(outgoingPrice);
                boxCount -= movement.getBoxCount();
            }
        }

        stock.setTotalQuantity(totalQuantity.max(BigDecimal.ZERO));
        stock.setBoxCount(Math.max(boxCount, 0));
        stock.setTotalValue(totalValue.max(BigDecimal.ZERO));
        stock.setLastPrice(lastPrice);

        productStockRepository.save(stock);
    }

    @Override
    public void increaseStock(Long productId, BigDecimal quantity, BigDecimal pricePerUnit, Unit unit, Integer boxCount) {
        ProductStock stock = getOrCreateStock(productId);

        BigDecimal additionalValue = pricePerUnit.multiply(quantity);
        BigDecimal newTotalValue = stock.getTotalValue().add(additionalValue);
        BigDecimal newTotalQuantity = stock.getTotalQuantity().add(quantity);
        int newBoxCount = stock.getBoxCount() + boxCount;

        stock.setTotalQuantity(newTotalQuantity);
        stock.setTotalValue(newTotalValue);
        stock.setBoxCount(newBoxCount);
        stock.setLastPrice(pricePerUnit);

        productStockRepository.save(stock);
    }

    @Override
    public void decreaseStock(Long productId, BigDecimal quantity, BigDecimal pricePerUnit, Unit unit, Integer boxCount) {
        ProductStock stock = getOrCreateStock(productId);

        if (stock.getTotalQuantity().compareTo(quantity) < 0 || stock.getBoxCount() < boxCount) {
            throw new IllegalStateException("Недостаточно товара на складе для списания");
        }

        BigDecimal subtractValue = pricePerUnit.multiply(quantity);
        BigDecimal newTotalValue = stock.getTotalValue().subtract(subtractValue);
        BigDecimal newTotalQuantity = stock.getTotalQuantity().subtract(quantity);
        int newBoxCount = stock.getBoxCount() - boxCount;

        stock.setTotalQuantity(newTotalQuantity);
        stock.setTotalValue(newTotalValue.max(BigDecimal.ZERO));
        stock.setBoxCount(newBoxCount);
        // Не обновляем lastPrice при расходе

        productStockRepository.save(stock);
    }

    @Override
    public List<StockResponse> getAll() {
        List<ProductStock> stocks = productStockRepository.findAll();
        List<StockResponse> responses = new ArrayList<>();
        for (ProductStock stock : stocks) {
            responses.add(new StockResponse(
                    stock.getId(),
                    stock.getTotalQuantity(),
                    stock.getLastPrice(),
                    stock.getBoxCount(),
                    stock.getTotalValue(),
                    stock.getCreatedAt()));
        }
        return responses;
    }

    @Override
    public StockResponse getStock(Long productId) {
        ProductStock stock = getOrCreateStock(productId);
        return new StockResponse(
                stock.getId(),
                stock.getTotalQuantity(),
                stock.getLastPrice(),
                stock.getBoxCount(),
                stock.getTotalValue(),
                stock.getCreatedAt());
    }

}
