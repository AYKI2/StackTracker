package stocktracker.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import stocktracker.exception.NotFoundException;
import stocktracker.model.dto.response.StockResponse;
import stocktracker.model.entity.Product;
import stocktracker.model.entity.ProductStock;
import stocktracker.model.enums.Unit;
import stocktracker.repository.ProductRepository;
import stocktracker.repository.ProductStockRepository;
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

    public ProductStockServiceImpl(ProductStockRepository productStockRepository, ProductRepository productRepository) {
        this.productStockRepository = productStockRepository;
        this.productRepository = productRepository;
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
                    newStock.setLastPrice(BigDecimal.ZERO);
                    newStock.setBoxCount(0);
                    newStock.setTotalValue(BigDecimal.ZERO);
                    newStock.setCreatedAt(LocalDateTime.now());
                    return productStockRepository.save(newStock);
                });
    }

    @Override
    public void increaseStock(Long productId, BigDecimal quantity, BigDecimal price, Unit unit) {
        ProductStock stock = getOrCreateStock(productId);
        stock.setTotalQuantity(stock.getTotalQuantity().add(quantity));
        stock.setLastPrice(price);
        if(unit == Unit.BOX) {
            stock.setBoxCount(quantity.intValue());
        }
        productStockRepository.save(stock);
    }

    @Override
    public void decreaseStock(Long productId, BigDecimal quantity, Unit unit) {
        ProductStock stock = getOrCreateStock(productId);
        if (stock.getTotalQuantity().compareTo(quantity) < 0) {
            throw new IllegalArgumentException("Недостаточно товара на складе");
        }
        stock.setTotalQuantity(stock.getTotalQuantity().subtract(quantity));
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
    public ProductStock getByProductId(Long productId) {
        return productStockRepository.findByProduct_Id(productId)
                .orElseThrow(() -> new NotFoundException("Остаток по товару не найден"));
    }

    @Override
    public StockResponse getStock(Long productId) {
        ProductStock stock = getByProductId(productId);
        return new StockResponse(
                stock.getId(),
                stock.getTotalQuantity(),
                stock.getLastPrice(),
                stock.getBoxCount(),
                stock.getTotalValue(),
                stock.getCreatedAt());
    }

}
