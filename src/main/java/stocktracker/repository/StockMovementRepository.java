package stocktracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import stocktracker.model.entity.StockMovement;
import stocktracker.model.enums.MovementType;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    @Query("""
            SELECT m FROM StockMovement m 
            WHERE (:productId IS NULL OR m.product.id = :productId) 
            AND (:type IS NULL OR m.type = :type) 
            AND (:fromDate IS NULL OR m.createdAt >= :fromDate) 
            AND (:toDate IS NULL OR m.createdAt <= :toDate) AND m.deleted = false """)
    List<StockMovement> filter(@Param("productId") Long productId, @Param("type") MovementType type, @Param("fromDate") LocalDateTime fromDate, @Param("toDate") LocalDateTime toDate );
}