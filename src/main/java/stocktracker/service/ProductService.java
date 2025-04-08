package stocktracker.service;

import org.springframework.http.ResponseEntity;
import stocktracker.model.dto.ProductDTO;
import stocktracker.model.dto.response.ProductResponse;

import java.util.List;

public interface ProductService {
    List<ProductResponse> getAllProducts();
    ProductResponse getProductById(Long id);
    ProductResponse createProduct(ProductDTO product);
    ProductResponse updateProduct(Long id, ProductDTO product);
    ResponseEntity<String> deleteProduct(Long id);
}
