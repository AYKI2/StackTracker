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
import stocktracker.model.entity.ProductStock;
import stocktracker.model.entity.StockMovement;
import stocktracker.model.enums.MovementType;
import stocktracker.repository.ProductRepository;
import stocktracker.repository.StockMovementRepository;
import stocktracker.service.ProductStockService;
import stocktracker.service.StockMovementService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class StockMovementServiceImpl implements StockMovementService {

    private final StockMovementRepository stockMovementRepository;
    private static final Logger logger = LoggerFactory.getLogger(StockMovementServiceImpl.class);
    private final ProductRepository productRepository;
    private final ProductStockService stockService;

    public StockMovementServiceImpl(StockMovementRepository stockMovementRepository, ProductRepository productRepository, ProductStockService stockService) {
        this.stockMovementRepository = stockMovementRepository;
        this.productRepository = productRepository;
        this.stockService = stockService;
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

    @Transactional
    @Override
    public StockMovementResponse create(StockMovementDTO request) {
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new NotFoundException("Товар не найден"));

        BigDecimal quantity = request.quantity();
        BigDecimal price = request.pricePerUnit(); // может быть null

        if (MovementType.valueOf(request.type().toUpperCase()) == MovementType.IN) {
            if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Цена обязательна для прихода");
            }
            stockService.increaseStock(product.getId(), quantity, price);
        } else if (MovementType.valueOf(request.type()) == MovementType.OUT) {
            stockService.decreaseStock(product.getId(), quantity);
            if (price != null) {
                throw new IllegalArgumentException("Нельзя указывать цену при расходе");
            }
        }

        StockMovement movement = new StockMovement(
                request.description(),
                quantity,
                price,
                MovementType.valueOf(request.type().toUpperCase()),
                product,
                LocalDateTime.now()
        );

        stockMovementRepository.save(movement);

        return new StockMovementResponse(
                movement.getId(),
                new ProductResponse(
                        product.getId(),
                        product.getName(),
                        product.getUnit().name(),
                        product.getCreatedAt()),
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
        logger.info("Soft delete движения товара с id: {}", id);
        StockMovement movement = stockMovementRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Движение не найдено"));

        // откатить изменение stock
        if (movement.getType() == MovementType.IN) {
            stockService.decreaseStock(movement.getProduct().getId(), movement.getQuantity());
        } else if (movement.getType() == MovementType.OUT) {
            ProductStock stock = stockService.getByProductId(movement.getProduct().getId());
            stockService.increaseStock( movement.getProduct().getId(), movement.getQuantity(), movement.getPricePerUnit() != null ? movement.getPricePerUnit() : stock.getLastPrice() );
        }

        movement.setDeleted(true);
        stockMovementRepository.save(movement);
        return ResponseEntity.ok("Движение помечено как удалённое и изменения откатились");
    }

    @Transactional
    @Override
    public StockMovementResponse update(Long id, StockMovementDTO request) {
        StockMovement oldMovement = stockMovementRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Операция не найдена"));
        // откат старого
        if (oldMovement.getType() == MovementType.IN) {
            stockService.decreaseStock(oldMovement.getProduct().getId(), oldMovement.getQuantity());
        } else {
            stockService.increaseStock(oldMovement.getProduct().getId(), oldMovement.getQuantity(), oldMovement.getPricePerUnit());
        }

// применим новый
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new NotFoundException("Товар не найден"));

        BigDecimal quantity = request.quantity();
        BigDecimal price = request.pricePerUnit();

        MovementType newType = MovementType.valueOf(request.type().toUpperCase());

        if (newType == MovementType.IN) {
            if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Цена обязательна при приходе");
            }
            stockService.increaseStock(product.getId(), quantity, price);
        } else {
            stockService.decreaseStock(product.getId(), quantity);
        }

        oldMovement.setType(newType);
        oldMovement.setProduct(product);
        oldMovement.setQuantity(quantity);
        oldMovement.setPricePerUnit(price);
        oldMovement.setDescription(request.description());
        oldMovement.setCreatedAt(LocalDateTime.now());

        stockMovementRepository.save(oldMovement);
        return new StockMovementResponse(
                oldMovement.getId(), new ProductResponse(
                        product.getId(), product.getName(),
                        product.getUnit().name(), product.getCreatedAt()),
                oldMovement.getType().name(), oldMovement.getQuantity(),
                oldMovement.getPricePerUnit(),
                oldMovement.getDescription(), oldMovement.getCreatedAt()
        );
    }
}
