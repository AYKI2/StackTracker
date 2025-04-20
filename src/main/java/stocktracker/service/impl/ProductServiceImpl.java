package stocktracker.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stocktracker.exception.NotFoundException;
import stocktracker.model.dto.ProductDTO;
import stocktracker.model.dto.response.CategoryResponse;
import stocktracker.model.dto.response.ProductResponse;
import stocktracker.model.dto.response.StockResponse;
import stocktracker.model.entity.Product;
import stocktracker.model.entity.ProductStock;
import stocktracker.model.enums.Unit;
import stocktracker.repository.ProductRepository;
import stocktracker.service.CategoryService;
import stocktracker.service.ProductService;
import stocktracker.service.ProductStockService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);
    private final CategoryService categoryService;
    private final ProductStockService productStockService;

    public ProductServiceImpl(ProductRepository productRepository, CategoryService categoryService, ProductStockService productStockService) {
        this.productRepository = productRepository;
        this.categoryService = categoryService;
        this.productStockService = productStockService;
    }

    @Override
    public List<ProductResponse> getAll() {
        logger.info("Получение списка всех товаров");
        List<Product> products = productRepository.findAll();

        return products.stream()
                .map(product -> {
                    ProductStock stock = productStockService.getOrCreateStock(product.getId());
                    return buildProductResponse(product, stock);
                })
                .toList();
    }

    @Override
    public ProductResponse getById(Long id) {
        logger.info("Получение товара по id: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Товар с id {} не найден", id);
                    return new NotFoundException("Товар не найден");
                });

        ProductStock stock = productStockService.getOrCreateStock(product.getId());
        return buildProductResponse(product, stock);
    }

    @Override
    public ProductResponse create(ProductDTO request) {
        logger.info("Создание нового товара: {}", request.name());

        Product product = new Product(
                request.name(),
                Unit.valueOf(request.unit()),
                request.unitsInBox(),
                request.pricePerUnit(),
                request.pricePerUnit().multiply(BigDecimal.valueOf(request.unitsInBox())),
                categoryService.findByName(request.categoryRequest().name()),
                LocalDateTime.now()
        );

        productRepository.save(product);
        productStockService.getOrCreateStock(product.getId());

        return buildProductResponse(product, productStockService.getOrCreateStock(product.getId()));
    }

    @Override
    public ProductResponse update(Long id, ProductDTO request) {
        logger.info("Обновление товара с id: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Товар не найден"));

        product.setName(request.name());
        product.setUnit(Unit.valueOf(request.unit()));
        product.setUnitPrice(request.pricePerUnit());
        product.setBoxPrice(request.pricePerUnit().multiply(BigDecimal.valueOf(request.unitsInBox())));
        product.setUnitsInBox(request.unitsInBox());
        product.setCategory(categoryService.findByName(request.categoryRequest().name()));

        productRepository.save(product);

        ProductStock stock = productStockService.getOrCreateStock(product.getId());
        return buildProductResponse(product, stock);
    }

    @Override
    public ResponseEntity<String> delete(Long id) {
        logger.info("Удаление товара с id: {}", id);
        if (!productRepository.existsById(id)) {
            logger.warn("Попытка удалить несуществующий товар с id: {}", id);
            throw new NotFoundException("Товар не найден");
        }
        productRepository.deleteById(id);
        return ResponseEntity.ok(String.format("Продукт с id: %d удален!", id));
    }

    private ProductResponse buildProductResponse(Product product, ProductStock stock) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getUnit().name(),
                product.getUnitPrice(),
                product.getBoxPrice(),
                product.getUnitsInBox(),
                new CategoryResponse(
                        product.getId(),
                        product.getCategory().getName()
                ),
                new StockResponse(
                        stock.getId(),
                        stock.getTotalQuantity(),
                        stock.getLastPrice(),
                        stock.getBoxCount(),
                        stock.getTotalValue(),
                        stock.getCreatedAt()
                ),
                product.getCreatedAt()
        );
    }
}
