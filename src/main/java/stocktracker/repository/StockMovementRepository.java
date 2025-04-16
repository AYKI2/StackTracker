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
    @Query(value = """
        SELECT * FROM stock_movements sm
        WHERE sm.deleted = false
          AND (CAST(:productId AS BIGINT) IS NULL OR sm.product_id = :productId)
          AND (CAST(:type AS VARCHAR) IS NULL OR sm.type = :type)
          AND (CAST(:startDate AS TIMESTAMP) IS NULL OR sm.created_at >= :startDate)
          AND (CAST(:endDate AS TIMESTAMP) IS NULL OR sm.created_at <= :endDate)
        ORDER BY sm.created_at DESC
    """, nativeQuery = true)
    List<StockMovement> filterMovements(
            @Param("productId") Long productId,
            @Param("type") String type,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}