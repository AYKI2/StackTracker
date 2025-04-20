package stocktracker.service;

import org.springframework.http.ResponseEntity;
import stocktracker.model.dto.request.CategoryRequest;
import stocktracker.model.dto.response.CategoryResponse;
import stocktracker.model.entity.Category;

import java.util.List;

public interface CategoryService {
    CategoryResponse create(CategoryRequest request);
    List<CategoryResponse> findAll();
    CategoryResponse findById(Long id);
    CategoryResponse update(Long id, CategoryRequest request);
    ResponseEntity<String> delete(Long id);
    Category findByName(String name);
}
