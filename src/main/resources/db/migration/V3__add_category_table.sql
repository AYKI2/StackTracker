-- Создание последовательности для категорий
CREATE SEQUENCE IF NOT EXISTS categories_id_seq START WITH 1 INCREMENT BY 1;

-- Создание таблицы categories
CREATE TABLE IF NOT EXISTS categories (
                            id BIGINT NOT NULL PRIMARY KEY DEFAULT nextval('categories_id_seq'),
                            name VARCHAR(255) NOT NULL UNIQUE
);

ALTER TABLE products ADD COLUMN IF NOT EXISTS category_id BIGINT;

ALTER TABLE products
    ADD CONSTRAINT fk_product_category
        FOREIGN KEY (category_id)
            REFERENCES categories(id)
            ON DELETE CASCADE;