package stocktracker.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import stocktracker.exception.NotFoundException;
import stocktracker.model.dto.request.CategoryRequest;
import stocktracker.model.dto.response.CategoryResponse;
import stocktracker.model.entity.Category;
import stocktracker.repository.CategoryRepository;
import stocktracker.service.CategoryService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private static CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        CategoryServiceImpl.categoryRepository = categoryRepository;
    }

    @Override
    public CategoryResponse create(CategoryRequest request) {
        if (categoryRepository.existsByNameIgnoreCase(request.name())) {
            throw new IllegalArgumentException("Категория уже существует");
        }
        Category category = categoryRepository.save(new Category(request.name()));
        return new CategoryResponse(category.getId(), category.getName());
    }

    @Override
    public List<CategoryResponse> findAll() {
        return categoryRepository.findAll().stream().map(category ->
                new CategoryResponse(
                        category.getId(),
                        category.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public CategoryResponse findById(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("Категория с id: %d не найден!", id)));
        return new CategoryResponse(category.getId(), category.getName());
    }

    @Override
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("Категория с id: %d не найден!", id)));
        category.setName(request.name());
        return new CategoryResponse(category.getId(), category.getName());
    }

    @Override
    public ResponseEntity<String> delete(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new EntityNotFoundException("Категория не найдена");
        }
        categoryRepository.deleteById(id);
        return new ResponseEntity<>(String.format("Категория с id: %d успешно удален!",id), HttpStatus.OK);
    }
}
