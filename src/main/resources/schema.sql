-- 交易表
CREATE TABLE IF NOT EXISTS t_r_transaction (
    id BIGINT PRIMARY KEY,
    account_number VARCHAR(50) NOT NULL,
    transaction_type VARCHAR(20) NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'CNY',
    description VARCHAR(500),
    category VARCHAR(50),
    status VARCHAR(20) DEFAULT 'COMPLETED',
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
