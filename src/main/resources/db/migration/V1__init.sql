-- users
CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       username VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       role VARCHAR(50) NOT NULL
);

-- products
CREATE TABLE products (
                          id BIGSERIAL PRIMARY KEY,
                          name VARCHAR(255) NOT NULL,
                          unit VARCHAR(50) NOT NULL,
                          created_at TIMESTAMP DEFAULT now()
);

-- stock_movements
CREATE TABLE stock_movements (
                                 id BIGSERIAL PRIMARY KEY,
                                 description TEXT,
                                 quantity NUMERIC(19, 4) NOT NULL,
                                 price_per_unit NUMERIC(19, 4),
                                 created_at TIMESTAMP DEFAULT now(),
                                 type VARCHAR(50) NOT NULL,
                                 product_id BIGINT NOT NULL,
                                 CONSTRAINT fk_stock_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

-- индексы
CREATE INDEX idx_stock_movements_product_id ON stock_movements(product_id);
