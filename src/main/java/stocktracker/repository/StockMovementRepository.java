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
        SELECT sm FROM StockMovement sm
        WHERE (:productId IS NULL OR sm.product.id = :productId)
        AND (:type IS NULL OR sm.type = :type)
        AND (:startDate IS NULL OR sm.createdAt >= :startDate)
        AND (:endDate IS NULL OR sm.createdAt <= :endDate)
        AND sm.deleted = false
        ORDER BY sm.createdAt DESC
    """)
    List<StockMovement> filterMovements(
            @Param("productId") Long productId,
            @Param("type") MovementType type,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}