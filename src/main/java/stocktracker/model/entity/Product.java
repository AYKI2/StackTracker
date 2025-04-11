package stocktracker.model.entity;

import jakarta.persistence.*;
import stocktracker.model.enums.Unit;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "products_id_gen")
    @SequenceGenerator(name = "products_id_gen", sequenceName = "products_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;
    private String name;
    @Enumerated(EnumType.STRING)
    private Unit unit;
    @Column
    private Integer unitsInBox; // сколько штук в одной коробке
    @Column(precision = 10, scale = 2)
    private BigDecimal unitPrice; // цена за штуку
    @Column(precision = 10, scale = 2)
    private BigDecimal boxPrice; // цена за коробку
    private Boolean boxPriceManual = false; // указывает, вручную ли введена boxPrice
    private LocalDateTime createdAt;

    public Product(String name, Unit unit, Integer unitsInBox, BigDecimal unitPrice, BigDecimal boxPrice, Boolean boxPriceManual, LocalDateTime createdAt) {
        this.name = name;
        this.unit = unit;
        this.unitsInBox = unitsInBox;
        this.unitPrice = unitPrice;
        this.boxPrice = boxPrice;
        this.boxPriceManual = boxPriceManual;
        this.createdAt = createdAt;
    }

    public Product() {}

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {}

    public Integer getUnitsInBox() {
        return unitsInBox;
    }

    public void setUnitsInBox(Integer unitsInBox) {
        this.unitsInBox = unitsInBox;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getBoxPrice() {
        return boxPrice;
    }

    public void setBoxPrice(BigDecimal boxPrice) {
        this.boxPrice = boxPrice;
    }

    public Boolean getBoxPriceManual() {
        return boxPriceManual;
    }

    public void setBoxPriceManual(Boolean boxPriceManual) {
        this.boxPriceManual = boxPriceManual;
    }

    // Автоматический пересчёт (в сервисе)
    @PrePersist
    @PreUpdate
    private void calculateBoxPriceIfNotManual() {
        if (Boolean.FALSE.equals(boxPriceManual) && unitPrice != null && unitsInBox != null) {
            boxPrice = unitPrice.multiply(BigDecimal.valueOf(unitsInBox));
        }
    }

}
