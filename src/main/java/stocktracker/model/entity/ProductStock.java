package stocktracker.model.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_stocks")
public class ProductStock {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_stocks_id_gen")
    @SequenceGenerator(name = "product_stocks_id_gen", sequenceName = "product_stocks_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;
    private BigDecimal totalQuantity;
    private BigDecimal lastPrice;
    private Integer boxCount;
    private BigDecimal totalValue; // totalQuantity * lastPrice
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    private LocalDateTime createdAt;

    public ProductStock(BigDecimal totalQuantity, BigDecimal lastPrice, Product product, LocalDateTime createdAt) {
        this.totalQuantity = totalQuantity;
        this.lastPrice = lastPrice;
        this.product = product;
        this.createdAt = createdAt;
    }

    public ProductStock(BigDecimal totalQuantity, BigDecimal lastPrice, Integer boxCount, BigDecimal totalValue, Product product, LocalDateTime createdAt) {
        this.totalQuantity = totalQuantity;
        this.lastPrice = lastPrice;
        this.boxCount = boxCount;
        this.totalValue = totalValue;
        this.product = product;
        this.createdAt = createdAt;
    }

    public ProductStock() {}


    public Long getId() {
        return id;
    }

    public BigDecimal getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(BigDecimal totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public BigDecimal getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(BigDecimal lastPrice) {
        this.lastPrice = lastPrice;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getBoxCount() {
        return boxCount;
    }

    public void setBoxCount(Integer boxCount) {
        this.boxCount = boxCount;
    }

    public BigDecimal getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(BigDecimal totalValue) {
        this.totalValue = totalValue;
    }
}
