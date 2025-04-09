package stocktracker.service;

import org.springframework.http.ResponseEntity;
import stocktracker.model.dto.ProductDTO;
import stocktracker.model.dto.response.ProductResponse;

import java.util.List;

public interface ProductService {
    List<ProductResponse> getAll();
    ProductResponse getById(Long id);
    ProductResponse create(ProductDTO product);
    ProductResponse update(Long id, ProductDTO product);
    ResponseEntity<String> delete(Long id);
}
