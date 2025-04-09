package stocktracker.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stocktracker.model.dto.StockMovementDTO;
import stocktracker.model.dto.response.StockMovementResponse;
import stocktracker.service.StockMovementService;

import java.util.List;

@RestController
@RequestMapping("/api/movements")
@Tag(name = "Stock Movement Controller", description = "Контроллер для упрвления и отслеживания движениями товаров!")
public class StockMovementController {

    private final StockMovementService stockMovementService;

    public StockMovementController(StockMovementService stockMovementService) {
        this.stockMovementService = stockMovementService;
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Получение информации о движении товара",
            description = "Возвращает полную информацию о Движениях товара по её идентификатору."
    )
    public ResponseEntity<StockMovementResponse> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(stockMovementService.getById(id));
    }

    @GetMapping
    @Operation(
            summary = "Получение списка Движений товаров",
            description = "Возвращает список Движений товаров с поддержкой пагинации."
    )
    public ResponseEntity<List<StockMovementResponse>> getAllProducts() {
        return ResponseEntity.ok(stockMovementService.getAll());
    }

    @PostMapping
    @Operation(
            summary = "Создание нового Движения для товара",
            description = "Создает новое Движение для товар с полученными данными и возвращает созданное движение."
    )
    public ResponseEntity<StockMovementResponse> create(@RequestBody StockMovementDTO request){
        return ResponseEntity.ok(stockMovementService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Обновление информации о Движении товара",
            description = "Обновляет данные Движений товара."
    )
    public ResponseEntity<StockMovementResponse> update(@PathVariable Long id, @RequestBody StockMovementDTO request){
        return ResponseEntity.ok(stockMovementService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Удаление Движений товара.",
            description = "Удаляет Движение товара с базы данных."
    )
    public ResponseEntity<String> delete(@PathVariable Long id){
        return stockMovementService.delete(id);
    }
}
