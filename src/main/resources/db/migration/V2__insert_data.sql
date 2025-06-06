-- Сброс последовательностей
ALTER SEQUENCE products_id_seq RESTART;
ALTER SEQUENCE product_stocks_id_seq RESTART;
ALTER SEQUENCE stock_movements_id_seq RESTART;

-- Вставка пользователей
INSERT INTO users (id, username, password, role)
VALUES
    (nextval('users_id_seq'), 'admin', '$2a$10$xtZCSEt../qlkI3q.Zh4zuo4xaEEjBYyfmJgyoONqTijqagDhVe0O', 'ADMIN');

-- Вставка продуктов (без category_id)
INSERT INTO products (id, name, unit, units_in_box, unit_price, box_price, created_at)
VALUES
    (nextval('products_id_seq'), 'Сахар', 'KG', 10, 85.00, 850.00, now()),
    (nextval('products_id_seq'), 'Мука', 'KG', 25, 65.00, 1625.00, now()),
    (nextval('products_id_seq'), 'Масло подсолнечное', 'LITER', 6, 140.00, 840.00, now()),
    (nextval('products_id_seq'), 'Чай черный', 'PACK', 20, 45.00, 900.00, now()),
    (nextval('products_id_seq'), 'Кофе растворимый', 'PIECE', 12, 220.00, 2640.00, now()),
    (nextval('products_id_seq'), 'Соль', 'KG', 20, 20.00, 400.00, now()),
    (nextval('products_id_seq'), 'Макароны', 'KG', 8, 50.00, 400.00, now()),
    (nextval('products_id_seq'), 'Гречка', 'KG', 15, 90.00, 1350.00, now()),
    (nextval('products_id_seq'), 'Молоко', 'LITER', 12, 60.00, 720.00, now()),
    (nextval('products_id_seq'), 'Йогурт', 'PIECE', 24, 30.00, 720.00, now());

-- Генерация записей в stock_movements и product_stocks
DO $$
    DECLARE
p RECORD;
BEGIN
FOR p IN SELECT * FROM products LOOP
    INSERT INTO stock_movements (
    id, description, price_per_unit,
    type, product_id, box_count, units_per_box,
    total_price, total_quantity, created_at, deleted
) VALUES (
             nextval('stock_movements_id_seq'),
             'Первоначальный приход для ' || p.name,
             p.unit_price, 'IN', p.id,
             20, p.units_in_box,
             p.unit_price * p.units_in_box, p.units_in_box * 20, now(), false
             );

INSERT INTO product_stocks (
    id, total_quantity, last_price, product_id,
    box_count, total_value, created_at
) VALUES (
             nextval('product_stocks_id_seq'),
             p.units_in_box * 20,
             p.unit_price,
             p.id,
             20,
             p.unit_price * p.units_in_box, now()
         );
END LOOP;
END $$;

-- Установка текущих значений последовательностей
SELECT setval('users_id_seq', 1, true);
SELECT setval('products_id_seq', 10, true);
SELECT setval('product_stocks_id_seq', 10, true);
SELECT setval('stock_movements_id_seq', 10, true);
