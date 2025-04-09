package stocktracker.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stocktracker.model.dto.ProductDTO;
import stocktracker.model.dto.response.ProductResponse;
import stocktracker.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Product Controller", description = "Контроллер для управления Товарами.")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Получение информации о Товаре",
            description = "Возвращает полную информацию о Товаре по её идентификатору."
    )
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getById(id));
    }

    @GetMapping
    @Operation(
            summary = "Получение списка Товаров",
            description = "Возвращает список Товаров с поддержкой пагинации."
    )
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        return ResponseEntity.ok(productService.getAll());
    }

    @PostMapping
    @Operation(
            summary = "Создание нового Товара",
            description = "Создает новый Товар с полученными данными и возвращает созданной товар."
    )
    public ResponseEntity<ProductResponse> create(@RequestBody ProductDTO productDTO){
        return ResponseEntity.ok(productService.create(productDTO));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Обновление информации о Товаре",
            description = "Обновляет данные Товара."
    )
    public ResponseEntity<ProductResponse> update(@PathVariable Long id, @RequestBody ProductDTO productDTO){
        return ResponseEntity.ok(productService.update(id, productDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Удаление товара.",
            description = "Удаляет товар с базы данных."
    )
    public ResponseEntity<String> delete(@PathVariable Long id){
        return productService.delete(id);
    }
}
