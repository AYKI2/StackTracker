package stocktracker.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stocktracker.exception.NotFoundException;
import stocktracker.model.dto.ProductDTO;
import stocktracker.model.dto.response.ProductResponse;
import stocktracker.model.entity.Product;
import stocktracker.model.enums.Unit;
import stocktracker.repository.ProductRepository;
import stocktracker.service.ProductService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<ProductResponse> getAll() {
        logger.info("Получение всех Товаров");
        List<Product> products = productRepository.findAll();
        logger.info("Найдено {} Товаров", products.size());

        List<ProductResponse> productResponses = new ArrayList<>();
        for (Product product : products) {
            productResponses.add(new ProductResponse(
                    product.getId(),product.getName(),
                    product.getUnit().name(),
                    product.getUnitPrice(),
                    product.getBoxPrice(),
                    product.getUnitsInBox(),
                    product.getCreatedAt()));
        }
        return productResponses;
    }

    @Override
    public ProductResponse getById(Long id) {
        logger.info("Поиск Товара с id: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Товар с id: {} не найден", id);
                    return new NotFoundException(String.format("Товар с id: %d не найден.", id));
                });
        logger.info("Товар с id: {} найден", id);
        return new ProductResponse(
                product.getId(),product.getName(),
                product.getUnit().name(),
                product.getUnitPrice(),
                product.getBoxPrice(),
                product.getUnitsInBox(),
                product.getCreatedAt());
    }

    @Override
    public ProductResponse create(ProductDTO request) {
        logger.info("Создание нового Товара: {}", request.name());

        BigDecimal pricePerUnit = request.pricePerUnit();
        Integer unitsInBox = request.unitsInBox();
        BigDecimal boxPrice = request.boxPrice();
        boolean boxPriceManual = false;

        if (boxPrice == null && pricePerUnit != null && unitsInBox != null) {
            boxPrice = pricePerUnit.multiply(BigDecimal.valueOf(unitsInBox));
        }else {
            boxPriceManual = true;
        }

        Product product = new Product(
                request.name(),
                Unit.valueOf(request.unit().toUpperCase()),
                unitsInBox,
                pricePerUnit,
                boxPrice,
                boxPriceManual,
                LocalDateTime.now()
        );

        productRepository.save(product);

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getUnit().name(),
                product.getUnitPrice(),
                product.getBoxPrice(),
                product.getUnitsInBox(),
                product.getCreatedAt()
        );
    }

    @Override
    public ProductResponse update(Long id, ProductDTO request) {
        logger.info("Обновление Товара с id: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Товар не найден."));
        boolean boxPriceManual = false;

        product.setName(request.name());
        product.setUnit(Unit.valueOf(request.unit().toUpperCase()));
        product.setUnitPrice(request.pricePerUnit());
        product.setUnitsInBox(request.unitsInBox());

        BigDecimal boxPrice = request.boxPrice();
        if (boxPrice == null && request.pricePerUnit() != null && request.unitsInBox() != null) {
            boxPrice = request.pricePerUnit().multiply(BigDecimal.valueOf(request.unitsInBox()));
        }else {
            boxPriceManual = true;
        }
        product.setBoxPriceManual(boxPriceManual);
        product.setBoxPrice(boxPrice);

        productRepository.save(product);

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getUnit().name(),
                product.getUnitPrice(),
                product.getBoxPrice(),
                product.getUnitsInBox(),
                product.getCreatedAt()
        );
    }

    @Transactional
    @Override
    public ResponseEntity<String> delete(Long id) {
        logger.info("Удаление Товара с id: {}", id);
        productRepository.deleteById(id);
        logger.info("Товар с id: {} успешно удален", id);
        return ResponseEntity.ok(String.format("Товар с id: %d успешно удален!", id));
    }
}
