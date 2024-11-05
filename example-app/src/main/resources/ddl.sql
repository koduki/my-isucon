-- 既存のシーケンスを削除
DROP SEQUENCE IF EXISTS user_sequence;
DROP SEQUENCE IF EXISTS card_sequence;
DROP SEQUENCE IF EXISTS transaction_sequence;

-- 既存のテーブルを削除
DROP TABLE IF EXISTS transaction_tbl;
DROP TABLE IF EXISTS card_tbl;
DROP TABLE IF EXISTS user_tbl;

-- ユーザーシーケンスの作成
CREATE SEQUENCE user_sequence START WITH 1;

-- ユーザーテーブルの作成
CREATE TABLE user_tbl (
    id BIGINT PRIMARY KEY DEFAULT nextval('user_sequence'),
    userName VARCHAR(255) UNIQUE NOT NULL,
    createdAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- カードシーケンスの作成
CREATE SEQUENCE card_sequence START WITH 1;

-- カードテーブルの作成
CREATE TABLE card_tbl (
    id BIGINT PRIMARY KEY DEFAULT nextval('card_sequence'),
    cardNumber VARCHAR(255) UNIQUE NOT NULL,
    limitAmount INTEGER NOT NULL,
    usedAmount INTEGER NOT NULL DEFAULT 0,
    createdAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id BIGINT REFERENCES user_tbl(id)
);

-- トランザクションシーケンスの作成
CREATE SEQUENCE transaction_sequence START WITH 1;

-- トランザクションテーブルの作成
CREATE TABLE transaction_tbl (
    id BIGINT PRIMARY KEY DEFAULT nextval('transaction_sequence'),
    cardNumber VARCHAR(255) NOT NULL,
    itemName VARCHAR(255) NOT NULL,
    amount INTEGER NOT NULL,
    transactionDate TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);