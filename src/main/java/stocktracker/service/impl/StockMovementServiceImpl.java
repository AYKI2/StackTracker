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
import stocktracker.model.dto.response.StockResponse;
import stocktracker.model.entity.Product;
import stocktracker.model.entity.ProductStock;
import stocktracker.model.entity.StockMovement;
import stocktracker.model.enums.MovementType;
import stocktracker.model.enums.Unit;
import stocktracker.repository.ProductRepository;
import stocktracker.repository.StockMovementRepository;
import stocktracker.service.ProductStockService;
import stocktracker.service.StockMovementService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
        logger.info("Получение всех передвижений товаров");
        return stockMovementRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
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
        return mapToResponse(movement);
    }

    @Transactional
    @Override
    public StockMovementResponse create(StockMovementDTO dto) {
        Product product = productRepository.findById(dto.productId())
                .orElseThrow(() -> new NotFoundException("Товар не найден"));

        MovementType type = MovementType.valueOf(dto.type().toUpperCase());
        int unitsPerBox = dto.unitsPerBox() != null ? dto.unitsPerBox() : product.getUnitsInBox();
        BigDecimal quantity = BigDecimal.valueOf(unitsPerBox).multiply(BigDecimal.valueOf(dto.boxCount()));
        BigDecimal price = dto.pricePerUnit() != null ? dto.pricePerUnit() : product.getUnitPrice();

        if (type == MovementType.IN && (price == null || price.compareTo(BigDecimal.ZERO) <= 0)) {
            throw new IllegalArgumentException("Цена обязательна для прихода");
        }

        // Применяем движение к складу
        if (type == MovementType.IN) {
            stockService.increaseStock(product.getId(), quantity, price, product.getUnit(), dto.boxCount());
        } else {
            stockService.decreaseStock(product.getId(), quantity, price, product.getUnit(), dto.boxCount());
        }

        BigDecimal totalPrice = type == MovementType.IN ? quantity.multiply(price) : null;

        StockMovement movement = new StockMovement(
                dto.description(),
                quantity,
                price,
                totalPrice,
                type,
                product,
                LocalDateTime.now(),
                dto.boxCount(),
                unitsPerBox
        );

        stockMovementRepository.save(movement);

        return mapToResponse(movement);
    }

    @Transactional
    @Override
    public ResponseEntity<String> delete(Long id) {
        StockMovement movement = stockMovementRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Передвижение не найдено"));

        rollbackStockMovement(movement);

        movement.setDeleted(true);
        stockMovementRepository.save(movement);

        return ResponseEntity.ok("Удалено и откат выполнен");
    }

    @Transactional
    @Override
    public StockMovementResponse update(Long id, StockMovementDTO dto) {
        StockMovement existing = stockMovementRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Движение не найдено"));

        // Просто обновляем существующее движение (не откатываем старое)
        Product product = productRepository.findById(dto.productId())
                .orElseThrow(() -> new NotFoundException("Товар не найден"));

        MovementType type = MovementType.valueOf(dto.type().toUpperCase());
        int unitsPerBox = dto.unitsPerBox() != null ? dto.unitsPerBox() : product.getUnitsInBox();
        BigDecimal quantity = BigDecimal.valueOf(unitsPerBox).multiply(BigDecimal.valueOf(dto.boxCount()));
        BigDecimal price = dto.pricePerUnit() != null ? dto.pricePerUnit() : product.getUnitPrice();
        BigDecimal totalPrice = type == MovementType.IN ? quantity.multiply(price) : null;

        existing.setProduct(product);
        existing.setType(type);
        existing.setBoxCount(dto.boxCount());
        existing.setUnitsPerBox(unitsPerBox);
        existing.setTotalQuantity(quantity);
        existing.setPricePerUnit(price);
        existing.setTotalPrice(totalPrice);
        existing.setDescription(dto.description());
        existing.setCreatedAt(LocalDateTime.now());

        stockMovementRepository.save(existing);

        // Обновим состояние склада заново (пересчёт внутри сервиса)
        stockService.recalculateStock(product.getId());

        return mapToResponse(existing);
    }

    @Override
    public List<StockMovementResponse> filter(Long productId, MovementType type, LocalDateTime startDate, LocalDateTime endDate) {
        return stockMovementRepository.filterMovements(productId, type.name(), startDate, endDate).stream()
                .map(this::mapToResponse)
                .toList();
    }

    private StockMovementResponse mapToResponse(StockMovement movement) {
        ProductStock stock = stockService.getOrCreateStock(movement.getProduct().getId());
        return new StockMovementResponse(
                movement.getId(),
                new ProductResponse(
                        movement.getProduct().getId(),
                        movement.getProduct().getName(),
                        movement.getProduct().getUnit().name(),
                        movement.getProduct().getUnitPrice(),
                        movement.getProduct().getBoxPrice(),
                        movement.getProduct().getUnitsInBox(),
                        new CategoryResponse(
                                movement.getProduct().getCategory().getId(),
                                movement.getProduct().getCategory().getName()
                        ),
                        new StockResponse(
                                stock.getId(),
                                stock.getTotalQuantity(),
                                stock.getLastPrice(),
                                stock.getBoxCount(),
                                stock.getTotalValue(),
                                stock.getCreatedAt()
                        ),
                        movement.getProduct().getCreatedAt()
                ),
                movement.getType().name(),
                movement.getTotalQuantity(),
                movement.getPricePerUnit(),
                movement.getTotalPrice(),
                movement.getDescription(),
                movement.getBoxCount(),
                movement.getUnitsPerBox(),
                movement.getCreatedAt()
        );
    }

    private void rollbackStockMovement(StockMovement movement) {
        Product product = movement.getProduct();
        BigDecimal quantity = movement.getTotalQuantity();
        BigDecimal price = movement.getPricePerUnit();
        Unit unit = product.getUnit();
        int boxCount = movement.getBoxCount();

        if (movement.getType() == MovementType.IN) {
            stockService.decreaseStock(product.getId(), quantity, price, unit, boxCount);
        } else {
            BigDecimal rollbackPrice = price != null ? price : stockService.getOrCreateStock(product.getId()).getLastPrice();
            stockService.increaseStock(product.getId(), quantity, rollbackPrice, unit, boxCount);
        }
    }
}
