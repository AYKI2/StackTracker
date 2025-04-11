-- products table
ALTER TABLE products ADD COLUMN units_in_box INTEGER NOT NULL DEFAULT 0;
ALTER TABLE products ADD COLUMN unit_price NUMERIC(19, 4) NOT NULL DEFAULT 0;
ALTER TABLE products ADD COLUMN box_price NUMERIC(19, 4) NOT NULL DEFAULT 0;
ALTER TABLE products ADD COLUMN box_price_manual BOOLEAN NOT NULL DEFAULT FALSE;

-- stock_movements table
ALTER TABLE stock_movements ADD COLUMN box_count INTEGER;
ALTER TABLE stock_movements ADD COLUMN units_per_box INTEGER;
ALTER TABLE stock_movements ADD COLUMN total_price NUMERIC(19, 4) NOT NULL DEFAULT 0;

-- product_stocks table
ALTER TABLE product_stocks ADD COLUMN box_count INTEGER;
ALTER TABLE product_stocks ADD COLUMN total_value NUMERIC(19, 4) NOT NULL DEFAULT 0;