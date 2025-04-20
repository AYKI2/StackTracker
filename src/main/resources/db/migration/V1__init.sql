-- users
CREATE TABLE IF NOT EXISTS users (
                                     id BIGSERIAL PRIMARY KEY,
                                     username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL
    );

-- categories
CREATE SEQUENCE IF NOT EXISTS categories_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS categories (
                                          id BIGINT PRIMARY KEY DEFAULT nextval('categories_id_seq'),
    name VARCHAR(255) NOT NULL UNIQUE
    );

-- products
CREATE TABLE IF NOT EXISTS products (
                                        id BIGSERIAL PRIMARY KEY,
                                        name VARCHAR(255) NOT NULL,
    unit VARCHAR(50) NOT NULL,
    units_in_box INTEGER NOT NULL DEFAULT 0,
    unit_price NUMERIC(19, 4) NOT NULL DEFAULT 0,
    box_price NUMERIC(19, 4) NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT now(),
    category_id BIGINT,
    CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
    );

-- stock_movements
CREATE TABLE IF NOT EXISTS stock_movements (
                                               id BIGSERIAL PRIMARY KEY,
                                               description TEXT,
                                               total_quantity NUMERIC(19, 4) NOT NULL,
    price_per_unit NUMERIC(19, 4),
    created_at TIMESTAMP DEFAULT now(),
    type VARCHAR(50) NOT NULL,
    box_count INTEGER,
    units_per_box INTEGER,
    total_price NUMERIC(19, 4) NOT NULL DEFAULT 0,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    product_id BIGINT NOT NULL,
    CONSTRAINT fk_stock_movements_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
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
    CONSTRAINT fk_product_stocks_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
    );

-- индексы
CREATE INDEX IF NOT EXISTS idx_stock_movements_product_id ON stock_movements(product_id);
