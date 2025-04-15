package stocktracker.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stocktracker.model.dto.request.CategoryRequest;
import stocktracker.model.dto.response.CategoryResponse;
import stocktracker.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@Tag(name = "Category Controller", description = "Контроллер для управления категориями")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    @Operation(
            summary = "Создание новой категории",
            description = "Создает новую категорию с полученными данными и возвращает созданную категорию."
    )
    public ResponseEntity<CategoryResponse> create(@RequestBody CategoryRequest request){
        return ResponseEntity.ok(categoryService.create(request));
    }

    @GetMapping
    @Operation(
            summary = "Получение списка Категорий товаров",
            description = "Возвращает список Движений товаров."
    )
    public ResponseEntity<List<CategoryResponse>> findAll(){
        return ResponseEntity.ok(categoryService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Получение информации о категории по его идентификатору",
            description = "Возвращает полную информацию о категории товара по её идентификатору."
    )
    public ResponseEntity<CategoryResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Обновление информации о категории товара",
            description = "Обновляет данные категории товара."
    )
    public ResponseEntity<CategoryResponse> update(@PathVariable Long id, @RequestBody CategoryRequest request){
        return ResponseEntity.ok(categoryService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Удаление категории.",
            description = "Удаляет категории товара с базы данных."
    )
    public ResponseEntity<String> delete(@PathVariable Long id){
        return categoryService.delete(id);
    }

}
