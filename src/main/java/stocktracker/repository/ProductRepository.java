package stocktracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stocktracker.model.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}