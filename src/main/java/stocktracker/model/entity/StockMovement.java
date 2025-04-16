package stocktracker.model.entity;

import jakarta.persistence.*;
import stocktracker.model.enums.MovementType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_movements")
public class StockMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "stock_movements_id_gen")
    @SequenceGenerator(name = "stock_movements_id_gen", sequenceName = "stock_movements_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;
    private String description;
    private BigDecimal totalQuantity;
    private BigDecimal pricePerUnit;
    private LocalDateTime createdAt;
    @Column(name = "deleted")
    private boolean deleted = false;
    @Enumerated(EnumType.STRING)
    private MovementType type;
    private Integer boxCount; // если поступает/расходуется коробками
    private Integer unitsPerBox; // фиксируется на момент движения (могло измениться потом)
    @Column(precision = 12, scale = 2)
    private BigDecimal totalPrice; // pricePerUnit * quantity
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    public StockMovement() {}

    public StockMovement(String description,
                         BigDecimal totalQuantity, BigDecimal price,
                         BigDecimal totalPrice, MovementType movementType,
                         Product product, LocalDateTime now, Integer boxCount, Integer unitsPerBox) {
        this.description = description;
        this.totalQuantity = totalQuantity;
        this.pricePerUnit = price;
        this.totalPrice = totalPrice;
        this.type = movementType;
        this.product = product;
        this.createdAt = now;
        this.boxCount = boxCount;
        this.unitsPerBox = unitsPerBox;
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(BigDecimal quantity) {
        this.totalQuantity = quantity;
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

    public Integer getBoxCount() {
        return boxCount;
    }

    public void setBoxCount(Integer boxCount) {
        this.boxCount = boxCount;
    }

    public Integer getUnitsPerBox() {
        return unitsPerBox;
    }

    public void setUnitsPerBox(Integer unitsPerBox) {
        this.unitsPerBox = unitsPerBox;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    @PrePersist
    @PreUpdate
    private void calculateTotal() {
        if (totalQuantity != null && pricePerUnit != null) {
            totalPrice = pricePerUnit.multiply(totalQuantity);
        }
    }
}
