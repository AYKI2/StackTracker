INSERT INTO categories (id, name)
VALUES
    (1,'Напитки'),
    (2, 'Молочная продукция'),
    (3,'Мороженное'),
    (4,'Кондитерские изделия'),
    (5,'Хозяйственные товары'),
    (6, 'Хлебобулочные изделия'),
    (7, 'Овощи и фрукты'),
    (8,'Продукты питания');

SELECT setval('categories_id_seq', 8, true);


UPDATE products SET category_id = 8 WHERE name IN ('Сахар', 'Мука', 'Масло подсолнечное', 'Соль', 'Макароны', 'Гречка');
UPDATE products SET category_id = 1 WHERE name IN ('Чай черный', 'Кофе растворимый');
UPDATE products SET category_id = 2 WHERE name IN ('Молоко', 'Йогурт');