# シンプルなペイメントシステム設計書

## システム概要

このペイメントシステムは、カード決済の基本動作である「オーソリ（限度額チェック）」「明細登録」「限度額更新」を実施します。利用者は、商品購入時にカード番号、商品名、価格を入力し、システムが与信枠内での購入可否を判定、購入が可能であれば利用明細に記録します。本システムではキャンセル処理は含みません。

---

## 要件・仕様

### ユーザー登録・カード管理

1. **ユーザーID**  
   各ユーザーに一意のIDを付与します（シーケンスを利用）。
2. **カード情報**  
   各ユーザーに対して以下の情報を保持：
   - カード番号（システムで自動生成、一意の16桁）
   - 限度額（初期設定：100,000円）
   - 使用残高（初期：0円）

---

### 購入リクエスト

1. **購入情報入力**  
   購入者が以下の情報を入力します：
   - カード番号
   - 商品名
   - 価格

2. **オーソリ処理**  
   システムが以下のチェックを行います：
   - 入力されたカード番号が有効であるか確認。
   - 指定された価格が「限度額 - 使用残高」以内であるかチェック。
   - 条件を満たせば「購入可」として明細登録へ進む。満たさない場合はエラーメッセージを返す。

3. **明細登録**  
   承認された取引について以下の情報をユーザーの明細に登録し、使用残高を更新します：
   - 購入日時
   - 商品名
   - 価格

4. **エラーハンドリング**  
   以下の条件でエラーメッセージを返します：
   - 無効なカード番号：存在しないカード番号が入力された場合。
   - 限度額オーバー：指定価格が「限度額 - 使用残高」を超過する場合。

---

### API仕様

#### 1. ユーザー登録API

- **エンドポイント**: `/register_user`
- **メソッド**: `POST`

##### リクエストボディ

```json
{
  "user_name": "string",
  "email": "string"
}
```

##### レスポンス

- 成功時

  ```json
  {
    "status": "success",
    "message": "User registered successfully",
    "user_id": "generated_user_id",
    "cards": [
      {
        "card_id": "generated_card_id",
        "card_number": "generated_card_number",
        "limit_amount": 100000,
        "used_amount": 0
      }
    ]
  }
  ```

- エラー時

  ```json
  {
    "status": "error",
    "message": "Error description"
  }
  ```

---

#### 2. カード追加API

- **エンドポイント**: `/add_card`
- **メソッド**: `POST`

##### リクエストボディ

```json
{
  "user_id": "string"
}
```

##### レスポンス

- 成功時

  ```json
  {
    "status": "success",
    "message": "Card added successfully",
    "card": {
      "card_id": "generated_card_id",
      "card_number": "generated_card_number",
      "limit_amount": 100000,
      "used_amount": 0
    }
  }
  ```

- エラー時

  ```json
  {
    "status": "error",
    "message": "Error description"
  }
  ```

---

## テーブル設計

### 1. テーブル: `users`

ユーザー情報を管理するテーブルです。`id` はシーケンスによって自動採番される主キーで、各ユーザーを一意に識別します。

| カラム名    | データ型       | 設定                          | 説明                       |
| ----------- | -------------- | ----------------------------- | -------------------------- |
| id          | SERIAL         | PRIMARY KEY                   | ユーザーの一意識別ID       |
| user_name   | VARCHAR(100)   | NOT NULL                      | ユーザー名                 |
| email       | VARCHAR(100)   | NOT NULL, UNIQUE              | メールアドレス             |
| created_at  | TIMESTAMP      | DEFAULT CURRENT_TIMESTAMP     | 登録日時                   |

---

### 2. テーブル: `cards`

カード情報を管理するテーブルです。`id` はシーケンスによって自動採番される主キーで、各カードを一意に識別します。`user_id` により `users` テーブルの `id` に紐づけられます。

| カラム名       | データ型       | 設定                                | 説明                                   |
| -------------- | -------------- | ----------------------------------- | -------------------------------------- |
| id             | SERIAL         | PRIMARY KEY                         | カードの一意識別ID                     |
| user_id        | INTEGER        | FOREIGN KEY (users.id)              | カード所有ユーザーのID                 |
| card_number    | CHAR(16)       | UNIQUE, NOT NULL                    | カード番号（システムで生成される一意の16桁） |
| limit_amount   | DECIMAL(10,2)  | DEFAULT 100000.00                   | カード限度額                           |
| used_amount    | DECIMAL(10,2)  | DEFAULT 0.00                        | 使用済み金額                           |
| created_at     | TIMESTAMP      | DEFAULT CURRENT_TIMESTAMP           | カード発行日時                         |

---

### 3. テーブル: `transactions`

取引（購入）情報を管理するテーブルです。各トランザクションはカードに紐づき、購入情報を記録します。`card_id` は `cards` テーブルの `id` に紐づけられます。

| カラム名          | データ型       | 設定                                 | 説明                               |
| ----------------- | -------------- | ------------------------------------ | ---------------------------------- |
| transaction_id    | SERIAL         | PRIMARY KEY                          | 取引の一意識別ID                   |
| card_id           | INTEGER        | FOREIGN KEY (cards.id)               | 取引が行われたカードのID           |
| item_name         | VARCHAR(255)   | NOT NULL                             | 商品名                             |
| amount            | DECIMAL(10,2)  | NOT NULL                             | 購入金額                           |
| transaction_date  | TIMESTAMP      | DEFAULT CURRENT_TIMESTAMP            | 取引日時                           |

---

### 4. テーブル: `limits_log` （オプショナル）

限度額の変更履歴を管理するテーブルです。`card_id` を `cards` テーブルの `id` に紐づけて、限度額の変更記録を残します。

| カラム名       | データ型       | 設定                                | 説明                               |
| -------------- | -------------- | ----------------------------------- | ---------------------------------- |
| log_id         | SERIAL         | PRIMARY KEY                         | 変更履歴の一意識別ID               |
| card_id        | INTEGER        | FOREIGN KEY (cards.id)              | 変更対象のカードID                 |
| old_limit      | DECIMAL(10,2)  | NOT NULL                            | 変更前の限度額                     |
| new_limit      | DECIMAL(10,2)  | NOT NULL                            | 変更後の限度額                     |
| changed_at     | TIMESTAMP      | DEFAULT CURRENT_TIMESTAMP           | 限度額が変更された日時             |

