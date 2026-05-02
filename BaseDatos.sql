-- Script accounts-service

-- Customers Table
CREATE TABLE IF NOT EXISTS customers (
                                         id BIGSERIAL PRIMARY KEY,
                                         name VARCHAR(255) NOT NULL,
    gender VARCHAR(50),
    age INTEGER,
    identification VARCHAR(50) NOT NULL UNIQUE,
    address VARCHAR(255),
    phone VARCHAR(50),
    password VARCHAR(255) NOT NULL,
    active BOOLEAN DEFAULT TRUE
    );

-- Accounts Table
CREATE TABLE IF NOT EXISTS accounts (
                                        id BIGSERIAL PRIMARY KEY,
                                        number VARCHAR(50) NOT NULL UNIQUE,
    type VARCHAR(50) NOT NULL,
    initial_balance DECIMAL(19, 2) NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    version BIGINT DEFAULT 0,
    customer_id BIGINT NOT NULL,
    CONSTRAINT fk_customer FOREIGN KEY (customer_id) REFERENCES customers(id)
    );

-- Movements Table
CREATE TABLE IF NOT EXISTS movements (
                                         id BIGSERIAL PRIMARY KEY,
                                         date TIMESTAMP NOT NULL,
                                         type VARCHAR(50) NOT NULL,
    amount DECIMAL(19, 2) NOT NULL,
    balance DECIMAL(19, 2) NOT NULL,
    idempotency_key VARCHAR(255) UNIQUE,
    account_id BIGINT NOT NULL,
    CONSTRAINT fk_account FOREIGN KEY (account_id) REFERENCES accounts(id)
    );

-- Customers for test
INSERT INTO customers (name, gender, age, identification, address, phone, password, active) VALUES
                                                                                                ('Jose Lema', 'M', 35, '1234567890', 'Otavalo sn y principal', '098254785', '1234', TRUE),
                                                                                                ('Marianela Montalvo', 'F', 32, '0987654321', 'Amazonas y NNUU', '097548965', '5678', TRUE),
                                                                                                ('Juan Osorio', 'M', 28, '1122334455', '13 junio y Equinoccial', '098874587', '1245', TRUE);

-- Accounts for test
INSERT INTO accounts (number, type, initial_balance, active, customer_id) VALUES
                                                                              ('478758', 'SAVINGS', 2000.00, TRUE, 1),
                                                                              ('225487', 'CHECKING', 100.00, TRUE, 2),
                                                                              ('495878', 'SAVINGS', 0.00, TRUE, 3),
                                                                              ('496825', 'SAVINGS', 540.00, TRUE, 2);
