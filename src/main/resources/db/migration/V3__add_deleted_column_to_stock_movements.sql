-- Add 'deleted' column to stock_movements
ALTER TABLE stock_movements ADD COLUMN deleted BOOLEAN NOT NULL DEFAULT FALSE;