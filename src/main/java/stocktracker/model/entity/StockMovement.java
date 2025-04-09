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
    @Column(name = "deleted")
    private boolean deleted = false;
    @Enumerated(EnumType.STRING)
    private MovementType type;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    public StockMovement(String description, BigDecimal quantity, BigDecimal pricePerUnit, MovementType type, Product product, LocalDateTime createdAt) {
        this.description = description;
        this.quantity = quantity;
        this.pricePerUnit = pricePerUnit;
        this.type = type;
        this.product = product;
        this.createdAt = createdAt;
    }

    public StockMovement() {}

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(BigDecimal pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public MovementType getType() {
        return type;
    }

    public void setType(MovementType type) {
        this.type = type;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
