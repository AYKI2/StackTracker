package stocktracker.model.entity;

import jakarta.persistence.*;
import stocktracker.model.enums.MovementType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_movements")
public class StockMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "stock_movement_id_gen")
    @SequenceGenerator(name = "stock_movement_id_gen", sequenceName = "stock_movement_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;
    private String description;
    private BigDecimal quantity;
    private BigDecimal pricePerUnit;
    private LocalDateTime createdAt;
    @Enumerated(EnumType.STRING)
    private MovementType type;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}
