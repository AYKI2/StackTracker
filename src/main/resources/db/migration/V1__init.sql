-- users
CREATE TABLE IF NOT EXISTS users (
                       id BIGSERIAL PRIMARY KEY,
                       username VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       role VARCHAR(50) NOT NULL
);

-- products
CREATE TABLE IF NOT EXISTS products (
                          id BIGSERIAL PRIMARY KEY,
                          name VARCHAR(255) NOT NULL,
                          unit VARCHAR(50) NOT NULL,
                          units_in_box INTEGER NOT NULL DEFAULT 0,
                          unit_price NUMERIC(19, 4) NOT NULL DEFAULT 0,
                          box_price NUMERIC(19, 4) NOT NULL DEFAULT 0,
                          box_price_manual BOOLEAN NOT NULL DEFAULT FALSE,
                          created_at TIMESTAMP DEFAULT now()
);

-- stock_movements
CREATE TABLE IF NOT EXISTS stock_movements (
                                 id BIGSERIAL PRIMARY KEY,
                                 description TEXT,
                                 quantity NUMERIC(19, 4) NOT NULL,
                                 price_per_unit NUMERIC(19, 4),
                                 created_at TIMESTAMP DEFAULT now(),
                                 type VARCHAR(50) NOT NULL,
                                 box_count INTEGER,
                                 units_per_box INTEGER,
                                 total_price NUMERIC(19, 4) NOT NULL DEFAULT 0,
                                 deleted BOOLEAN NOT NULL DEFAULT FALSE,
                                 product_id BIGINT NOT NULL,
                                 CONSTRAINT fk_stock_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

-- product_stocks
CREATE TABLE IF NOT EXISTS product_stocks (
                                id BIGSERIAL PRIMARY KEY,
                                total_quantity NUMERIC(19, 4) NOT NULL,
                                last_price NUMERIC(19, 4) NOT NULL,
                                box_count INTEGER,
                                total_value NUMERIC(19, 4) NOT NULL DEFAULT 0,
                                created_at TIMESTAMP DEFAULT now(),
                                product_id BIGINT NOT NULL,
                                CONSTRAINT fk_stock_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);


-- индексы
CREATE INDEX IF NOT EXISTS idx_stock_movements_product_id ON stock_movements(product_id);
