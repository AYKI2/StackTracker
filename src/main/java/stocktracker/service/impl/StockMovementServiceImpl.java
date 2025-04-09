package stocktracker.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stocktracker.exception.NotFoundException;
import stocktracker.model.dto.StockMovementDTO;
import stocktracker.model.dto.response.ProductResponse;
import stocktracker.model.dto.response.StockMovementResponse;
import stocktracker.model.entity.Product;
import stocktracker.model.entity.StockMovement;
import stocktracker.model.enums.MovementType;
import stocktracker.repository.ProductRepository;
import stocktracker.repository.StockMovementRepository;
import stocktracker.service.StockMovementService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class StockMovementServiceImpl implements StockMovementService {

    private final StockMovementRepository stockMovementRepository;
    private static final Logger logger = LoggerFactory.getLogger(StockMovementServiceImpl.class);
    private final ProductRepository productRepository;

    public StockMovementServiceImpl(StockMovementRepository stockMovementRepository, ProductRepository productRepository) {
        this.stockMovementRepository = stockMovementRepository;
        this.productRepository = productRepository;
    }

    @Override
    public List<StockMovementResponse> getAll() {
        logger.info("Получение данных о всех передвижениях товаров");
        List<StockMovement> movements = stockMovementRepository.findAll();
        logger.info("Найдено {} передвижений товаров", movements.size());

        List<StockMovementResponse> movementResponses = new ArrayList<>();
        for (StockMovement movement : movements) {
            movementResponses.add(new StockMovementResponse(
                    movement.getId(),
                    new ProductResponse(
                            movement.getProduct().getId(),
                            movement.getProduct().getName(),
                            movement.getProduct().getUnit().name(),
                            movement.getProduct().getCreatedAt()
                    ),
                    movement.getType().name(),
                    movement.getQuantity(),
                    movement.getPricePerUnit(),
                    movement.getDescription(),
                    movement.getCreatedAt()));
        }
        return movementResponses;
    }

    @Override
    public StockMovementResponse getById(Long id) {
        logger.info("Поиск движений товара с id: {}", id);
        StockMovement movement = stockMovementRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Движение товара с id: {} не найден", id);
                    return new NotFoundException(String.format("Движение товара с id: %d не найден.", id));
                });
        logger.info("Движение товара с id: {} найдено", id);
        return new StockMovementResponse(
                movement.getId(),
                new ProductResponse(
                        movement.getProduct().getId(),
                        movement.getProduct().getName(),
                        movement.getProduct().getUnit().name(),
                        movement.getProduct().getCreatedAt()
                ),
                movement.getType().name(),
                movement.getQuantity(),
                movement.getPricePerUnit(),
                movement.getDescription(),
                movement.getCreatedAt());
    }

    @Override
    public StockMovementResponse create(StockMovementDTO request) {
        logger.info("Поиск Товара с id: {}", request.productId());
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> {
                    logger.error("Товар с id: {} не найден", request.productId());
                    return new NotFoundException(String.format("Товар с id: %d не найден.", request.productId()));
                });
        logger.info("Создание нового движения для товара с именем: {}", product.getName());
        StockMovement movement = stockMovementRepository.save(new StockMovement(request.description(), request.quantity(), request.pricePerUnit(), MovementType.valueOf(request.type().toUpperCase()), product,LocalDateTime.now()));
        logger.info("Движения товара с id: {} успешно создан", movement.getId());
        return new StockMovementResponse(
                movement.getId(),
                new ProductResponse(
                        movement.getProduct().getId(),
                        movement.getProduct().getName(),
                        movement.getProduct().getUnit().name(),
                        movement.getProduct().getCreatedAt()
                ),
                movement.getType().name(),
                movement.getQuantity(),
                movement.getPricePerUnit(),
                movement.getDescription(),
                movement.getCreatedAt()
        );
    }

    @Override
    public StockMovementResponse update(Long id, StockMovementDTO request) {
        logger.info("Поиск Товара с id: {}", request.productId());
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> {
                    logger.error("Товар с id: {} не найден", request.productId());
                    return new NotFoundException(String.format("Товар с id: %d не найден.", request.productId()));
                });
        logger.info("Товар с id: {} найден", request.productId());


        logger.info("Обновление движения товара с id: {}", id);
        StockMovement movement = stockMovementRepository.findById(id).orElseThrow(() -> {
            logger.error("Движение товара с id: {} не найден для обновления", id);
            return new NotFoundException(String.format("Движение товара с id: %d не найден.", id));
        });
        movement.setDescription(request.description());
        movement.setType(MovementType.valueOf(request.type().toUpperCase()));
        movement.setQuantity(request.quantity());
        movement.setPricePerUnit(request.pricePerUnit());
        movement.setProduct(product);
        StockMovement updatedMovement = stockMovementRepository.save(movement);
        logger.info("Товар с id: {} успешно обновлён", updatedMovement.getId());
        return new StockMovementResponse(
                movement.getId(),
                new ProductResponse(
                        movement.getProduct().getId(),
                        movement.getProduct().getName(),
                        movement.getProduct().getUnit().name(),
                        movement.getProduct().getCreatedAt()
                ),
                movement.getType().name(),
                movement.getQuantity(),
                movement.getPricePerUnit(),
                movement.getDescription(),
                movement.getCreatedAt()
        );
    }

    @Transactional
    @Override
    public ResponseEntity<String> delete(Long id) {
        logger.info("Удаление движения товара с id: {}", id);
        stockMovementRepository.deleteById(id);
        logger.info("Движение товара с id: {} успешно удален", id);
        return ResponseEntity.ok(String.format("Движение товара с id: %d успешно удален!", id));
    }
}
