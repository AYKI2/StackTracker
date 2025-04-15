package stocktracker.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stocktracker.exception.NotFoundException;
import stocktracker.model.dto.StockMovementDTO;
import stocktracker.model.dto.response.CategoryResponse;
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
                            movement.getProduct().getId(),movement.getProduct().getName(),
                            movement.getProduct().getUnit().name(),
                            movement.getProduct().getUnitPrice(),
                            movement.getProduct().getBoxPrice(),
                            movement.getProduct().getUnitsInBox(),
                            new CategoryResponse(
                                    movement.getProduct().getId(),
                                    movement.getProduct().getCategory().getName()),
                            movement.getProduct().getCreatedAt()
                    ),
                    movement.getType().name(),
                    movement.getQuantity(),
                    movement.getPricePerUnit(),
                    movement.getTotalPrice(),
                    movement.getDescription(),
                    movement.getCreatedAt())
            );
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
                        movement.getProduct().getId(),movement.getProduct().getName(),
                        movement.getProduct().getUnit().name(),
                        movement.getProduct().getUnitPrice(),
                        movement.getProduct().getBoxPrice(),
                        movement.getProduct().getUnitsInBox(),
                        new CategoryResponse(
                                movement.getProduct().getId(),
                                movement.getProduct().getCategory().getName()),
                        movement.getProduct().getCreatedAt()
                ),
                movement.getType().name(),
                movement.getQuantity(),
                movement.getPricePerUnit(),
                movement.getTotalPrice(),
                movement.getDescription(),
                movement.getCreatedAt()
        );
    }

    @Transactional
    @Override
    public StockMovementResponse create(StockMovementDTO request) {
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new NotFoundException("Товар не найден"));

        BigDecimal quantity = request.quantity();
        BigDecimal price = request.pricePerUnit(); // может быть null

        BigDecimal totalPrice = null;
        if (MovementType.valueOf(request.type().toUpperCase()) == MovementType.IN) {
            if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Цена обязательна для прихода");
            }
            stockService.increaseStock(product.getId(), quantity, price, product.getUnit());
            totalPrice = quantity.multiply(price);
        } else if (MovementType.valueOf(request.type()) == MovementType.OUT) {
            stockService.decreaseStock(product.getId(), quantity, product.getUnit());
            if (price != null) {
                throw new IllegalArgumentException("Нельзя указывать цену при расходе");
            }
        }

        StockMovement movement = new StockMovement(
                request.description(),
                quantity,
                price,
                totalPrice,
                MovementType.valueOf(request.type().toUpperCase()),
                product,
                LocalDateTime.now(),
                request.boxCount(),
                request.unitsPerBox()
        );

        stockMovementRepository.save(movement);

        return new StockMovementResponse(
                movement.getId(),
                new ProductResponse(
                        product.getId(),product.getName(),
                        product.getUnit().name(),
                        product.getUnitPrice(),
                        product.getBoxPrice(),
                        product.getUnitsInBox(),
                        new CategoryResponse(
                                movement.getProduct().getId(),
                                movement.getProduct().getCategory().getName()),
                        product.getCreatedAt()
                ),
                movement.getType().name(),
                movement.getQuantity(),
                movement.getPricePerUnit(),
                movement.getTotalPrice(),
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
            stockService.decreaseStock(movement.getProduct().getId(), movement.getQuantity(), movement.getProduct().getUnit());
        } else if (movement.getType() == MovementType.OUT) {
            ProductStock stock = stockService.getByProductId(movement.getProduct().getId());
            stockService.increaseStock( movement.getProduct().getId(), movement.getQuantity(), movement.getPricePerUnit() != null ? movement.getPricePerUnit() : stock.getLastPrice(), movement.getProduct().getUnit());
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
            stockService.decreaseStock(oldMovement.getProduct().getId(), oldMovement.getQuantity(), oldMovement.getProduct().getUnit());
        } else {
            stockService.increaseStock(oldMovement.getProduct().getId(), oldMovement.getQuantity(), oldMovement.getPricePerUnit(), oldMovement.getProduct().getUnit());
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
            stockService.increaseStock(product.getId(), quantity, price, product.getUnit());
        } else {
            stockService.decreaseStock(product.getId(), quantity, product.getUnit());
        }

        oldMovement.setType(newType);
        oldMovement.setProduct(product);
        oldMovement.setQuantity(quantity);
        oldMovement.setPricePerUnit(price);
        oldMovement.setDescription(request.description());
        oldMovement.setCreatedAt(LocalDateTime.now());
        oldMovement.setBoxCount(request.boxCount());
        oldMovement.setUnitsPerBox(request.unitsPerBox());

        if (newType == MovementType.IN && price != null && quantity != null) {
            oldMovement.setTotalPrice(quantity.multiply(price));
        } else {
            oldMovement.setTotalPrice(null);
        }

        stockMovementRepository.save(oldMovement);
        return new StockMovementResponse(
                oldMovement.getId(),
                new ProductResponse(
                        product.getId(),product.getName(),
                        product.getUnit().name(),
                        product.getUnitPrice(),
                        product.getBoxPrice(),
                        product.getUnitsInBox(),
                        new CategoryResponse(
                                product.getId(),
                                product.getCategory().getName()),
                        product.getCreatedAt()
                ),
                oldMovement.getType().name(),
                oldMovement.getQuantity(),
                oldMovement.getPricePerUnit(),
                oldMovement.getTotalPrice(),
                oldMovement.getDescription(),
                oldMovement.getCreatedAt()
        );
    }

    @Override
    public List<StockMovementResponse> filter(Long productId, MovementType type, LocalDateTime startDate, LocalDateTime endDate) {
        List<StockMovement> movements = stockMovementRepository.filterMovements(productId, type, startDate, endDate);

        return movements.stream()
                .map(sm -> new StockMovementResponse(
                        sm.getId(),
                        new ProductResponse(
                                sm.getProduct().getId(),
                                sm.getProduct().getName(),
                                sm.getProduct().getUnit().name(),
                                sm.getProduct().getUnitPrice(),
                                sm.getProduct().getBoxPrice(),
                                sm.getProduct().getUnitsInBox(),
                                new CategoryResponse(
                                        sm.getProduct().getId(),
                                        sm.getProduct().getCategory().getName()
                                ),
                                sm.getProduct().getCreatedAt()
                        ),
                        sm.getType().name(),
                        sm.getQuantity(),
                        sm.getPricePerUnit(),
                        sm.getTotalPrice(),
                        sm.getDescription(),
                        sm.getCreatedAt()
                ))
                .toList();
    }

}
