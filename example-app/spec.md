# シンプルなペイメントシステム設計書

## システム概要

このペイメントシステムは、カード決済の基本動作である「オーソリ（与信枠チェック）」「明細登録」を実施します。利用者は、商品購入時にカード番号、商品名、価格を入力し、システムが与信枠内での購入可否を判定、購入が可能であれば利用明細に記録します。

---
## 制約/前提

テストプログラムのため以下の制約/前提があります。

- キャンセル処理は含みません
- 加盟店関連の処理の考慮は不要です
- 認証などセキュリティの実装は不要です

## 要件・仕様

### ユーザー登録・カード管理

1. **ユーザー情報**  
   - 各ユーザーに一意のIDを付与します。
   - 一意のユーザ名を持ちます。

2. **カード情報**  
   - 各ユーザーは複数のカードを所有できます。
   - 各カードには以下の情報を保持します：
     - カード番号（16桁のランダムな正の整数）
     - 限度額（100万円 ~ 1,000万円の範囲でランダムに設定される正の整数）
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
   - 条件を満たせば「購入可」として購入処理へ進む。満たさない場合はエラーメッセージを返す。

3. **購入処理**  
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
  "user_name": "string"
}
```

##### レスポンス

- 成功時

  ```json
  {
    "status": "success",
    "message": "User registered successfully",
    "user_id": "generated_user_id",
    "user_name": "Taro",
    "cards": [
      {
        "card_id": "generated_card_id",
        "card_number": "generated_card_number",
        "limit_amount": 1000000,
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
      "limit_amount": 1000000,
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
| user_name   | VARCHAR(100)   | NOT NULL, UNIQUE              | 一意のユーザー名           |
| created_at  | TIMESTAMP      | DEFAULT CURRENT_TIMESTAMP     | 登録日時                   |
| updated_at  | TIMESTAMP      | DEFAULT CURRENT_TIMESTAMP     | 更新日時                   |

---

### 2. テーブル: `cards`

カード情報を管理するテーブルです。`id` はシーケンスによって自動採番される主キーで、各カードを一意に識別します。`user_id` により `users` テーブルの `id` に紐づけられます。

| カラム名       | データ型       | 設定                                | 説明                                   |
| -------------- | -------------- | ----------------------------------- | -------------------------------------- |
| id             | SERIAL         | PRIMARY KEY                         | カードの一意識別ID                     |
| user_id        | INTEGER        | FOREIGN KEY (users.id)              | カード所有ユーザーのID                 |
| card_number    | CHAR(16)       | UNIQUE, NOT NULL                    | カード番号（システムで生成される一意の16桁） |
| limit_amount   | INTEGER        | NOT NULL                            | カードごとの限度額（100万~1000万の範囲） |
| used_amount    | INTEGER        | DEFAULT 0                           | 使用済み金額                           |
| created_at     | TIMESTAMP      | DEFAULT CURRENT_TIMESTAMP           | カード発行日時                         |
| updated_at     | TIMESTAMP      | DEFAULT CURRENT_TIMESTAMP           | 更新日時                              |
---

### 3. テーブル: `transactions`

取引（購入）情報を管理するテーブルです。各トランザクションはカードに紐づき、購入情報を記録します。`card_id` は `cards` テーブルの `id` に紐づけられます。

| カラム名          | データ型       | 設定                                 | 説明                               |
| ----------------- | -------------- | ------------------------------------ | ---------------------------------- |
| id                | SERIAL         | PRIMARY KEY                          | 取引の一意識別ID                   |
| card_id           | INTEGER        | FOREIGN KEY (cards.id)               | 取引が行われたカードのID           |
| item_name         | VARCHAR(255)   | NOT NULL                             | 商品名                             |
| amount            | INTEGER        | NOT NULL                             | 購入金額                           |
| transaction_date  | TIMESTAMP      | DEFAULT CURRENT_TIMESTAMP            | 取引日時                           |
