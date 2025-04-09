package stocktracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stocktracker.model.entity.ProductStock;

import java.util.Optional;

@Repository
public interface ProductStockRepository extends JpaRepository<ProductStock, Long> {
    Optional<ProductStock> findByProduct_Id(Long productId);
}