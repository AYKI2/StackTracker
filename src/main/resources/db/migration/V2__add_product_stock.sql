-- product_stocks
CREATE TABLE product_stocks (
                                id BIGSERIAL PRIMARY KEY,
                                total_quantity NUMERIC(19, 4) NOT NULL,
                                last_price NUMERIC(19, 4) NOT NULL,
                                created_at TIMESTAMP DEFAULT now(),
                                product_id BIGINT NOT NULL,
                                CONSTRAINT fk_stock_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);
