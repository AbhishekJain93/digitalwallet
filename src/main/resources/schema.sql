CREATE TABLE wallets (
    id   INTEGER     NOT NULL,
    balance INTEGER NOT NULL,
    currency_code VARCHAR(3) NOT NULL,
    user_id INTEGER NOT NULL,
   created_at TIMESTAMP DEFAULT NOW() ,
  updated_at TIMESTAMP DEFAULT NOW(),
    PRIMARY KEY (id)
);


CREATE TABLE transactions (
    id    UUID     NOT NULL,
    transaction_ref VARCHAR(36) NOT NULL,
    amount INTEGER NOT NULL,
    currency_code VARCHAR(3) NOT NULL,
    wallet_id INTEGER NOT NULL,
   created_at TIMESTAMP DEFAULT NOW() ,
  updated_at TIMESTAMP DEFAULT NOW() ,
    PRIMARY KEY (id)
);