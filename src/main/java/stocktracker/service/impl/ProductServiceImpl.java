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
        return new ProductResponse(product.getId(), product.getName(), product.getUnit().name(), product.getCreatedAt());
    }

    @Override
    public ProductResponse create(ProductDTO request) {
        logger.info("Создание нового Товара с именем: {}", request.name());
        Product product = productRepository.save(new Product(request.name(), Unit.valueOf(request.unit().toUpperCase()), LocalDateTime.now()));
        logger.info("Товар с id: {} успешно создан", product.getId());
        return new ProductResponse(product.getId(), product.getName(), product.getUnit().name(), product.getCreatedAt());
    }

    @Override
    public ProductResponse update(Long id, ProductDTO request) {
        logger.info("Обновление Товара с id: {}", id);
        Product product = productRepository.findById(id).orElseThrow(() -> {
            logger.error("Товар с id: {} не найден для обновления", id);
            return new NotFoundException(String.format("Товар с id: %d не найден.", id));
        });
        product.setName(request.name());
        product.setUnit(Unit.valueOf(request.unit().toUpperCase()));
        Product updatedProduct = productRepository.save(product);
        logger.info("Товар с id: {} успешно обновлён", updatedProduct.getId());
        return new ProductResponse(updatedProduct.getId(), updatedProduct.getName(), updatedProduct.getUnit().name(), updatedProduct.getCreatedAt());
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
