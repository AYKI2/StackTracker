-- Безопасное добавление внешнего ключа для category_id (если миграция выполняется повторно)
DO $$
    BEGIN
        IF NOT EXISTS (
            SELECT 1
            FROM information_schema.table_constraints
            WHERE constraint_type = 'FOREIGN KEY'
              AND table_name = 'products'
              AND constraint_name = 'fk_product_category'
        ) THEN
            ALTER TABLE products
                ADD CONSTRAINT fk_product_category
                    FOREIGN KEY (category_id)
                        REFERENCES categories(id)
                        ON DELETE CASCADE;
        END IF;
    END
$$;
