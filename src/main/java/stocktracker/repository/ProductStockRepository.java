package stocktracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import stocktracker.model.dto.response.StockResponse;
import stocktracker.model.entity.ProductStock;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductStockRepository extends JpaRepository<ProductStock, Long> {
    @Query("SELECT s FROM ProductStock s WHERE s.product.id = :id")
    Optional<ProductStock> findByProduct_Id(@Param("id") Long productId);
}