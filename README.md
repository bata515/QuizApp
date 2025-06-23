# クイズアプリ

Spring Boot + PostgreSQL を使用したクイズアプリケーションです。プレイヤーはログイン不要でクイズに挑戦でき、管理者はログイン後にクイズを編集・管理できます。

## 機能

### プレイヤー機能
- ログイン不要でクイズに参加
- カテゴリーを選択してクイズに挑戦
- 4択問題（複数正解対応）
- ランダムに問題が出題される
- 回答後に正解・不正解と解説を表示
- カテゴリー内の全問題回答後に結果表示

### 管理者機能
- JWT認証によるログイン
- クイズの追加・編集・削除
- カテゴリーの追加・編集・削除
- 問題の解説登録
- 複数正解対応の4択問題作成

## 技術スタック

- **バックエンド**: Spring Boot(Java)
- **データベース**: PostgreSQL
- **認証**: JWT (管理者用)、UUID Cookie (プレイヤー用)
- **フロントエンド**: HTML/CSS/JavaScript 
- **ビルドツール**: Maven
- **デプロイ**: Docker 、Render

## 開発環境セットアップ
1. リポジトリをクローンします。
2. `.env`ファイルを作成し、データベースの接続情報を設定します。
3. Docker Composeを使用してアプリケーションを起動します。

```bash
docker-compose up --build
```
4. ブラウザで`http://localhost:8080`にアクセスします。

5. 起動停止は以下のコマンドで行います。

```bash
# コンテナを停止削除する。バックグラウンド起動時でも止められる
docker-compose down
```
もしくは、ctrl + Cで停止できます(フォアグラウンド実行のみ停止可能)。

3. アプリケーションにアクセス
- プレイヤー画面: http://localhost:8080
- 管理者画面: http://localhost:8080/admin

## 初期データ

アプリケーション起動時に以下の初期データが自動で投入されます

#### 管理者アカウント
- ユーザー名: `admin`
- パスワード: `password`

#### カテゴリー
サンプルカテゴリーが投入されます。

#### サンプルクイズ
各カテゴリーにサンプル問題が投入されます。

## API一覧

### プレイヤー画面API

| エンドポイント | メソッド | 説明 |
|---|---|---|
| `/api/categories` | GET | カテゴリー一覧取得 |
| `/api/quizzes?category={id}` | GET | カテゴリー内のランダムクイズ取得 |
| `/api/quizzes/{id}/answer` | POST | クイズ回答送信 |
| `/api/result?category={id}` | GET | カテゴリー内のクイズ結果取得 |

### 管理者画面API

| エンドポイント | メソッド | 説明 | 認証 |
|---|---|---|---|
| `/api/admin/login` | POST | 管理者ログイン | 不要 |
| `/api/admin/categories` | GET | カテゴリー一覧取得 | 必要 |
| `/api/admin/categories` | POST | カテゴリー作成 | 必要 |
| `/api/admin/categories/{id}` | PUT | カテゴリー更新 | 必要 |
| `/api/admin/categories/{id}` | DELETE | カテゴリー削除 | 必要 |
| `/api/admin/quizzes` | GET | クイズ一覧取得 | 必要 |
| `/api/admin/quizzes/{id}` | GET | クイズ詳細取得 | 必要 |
| `/api/admin/quizzes` | POST | クイズ作成 | 必要 |
| `/api/admin/quizzes/{id}` | PUT | クイズ更新 | 必要 |
| `/api/admin/quizzes/{id}` | DELETE | クイズ削除 | 必要 |

## データベース構成

```sql
-- カテゴリーテーブル
CREATE TABLE categories (
    id SERIAL PRIMARY KEY,
    name VARCHAR NOT NULL
);

-- クイズテーブル
CREATE TABLE quizzes (
    id SERIAL PRIMARY KEY,
    question TEXT NOT NULL,
    explanation TEXT,
    category_id INT REFERENCES categories(id)
);

-- 選択肢テーブル
CREATE TABLE choices (
    id SERIAL PRIMARY KEY,
    quiz_id INT REFERENCES quizzes(id),
    text TEXT NOT NULL,
    is_correct BOOLEAN NOT NULL
);

-- クイズ回答履歴テーブル
CREATE TABLE quiz_attempts (
    id SERIAL PRIMARY KEY,
    session_id VARCHAR NOT NULL,
    quiz_id INT REFERENCES quizzes(id),
    is_correct BOOLEAN NOT NULL
);

-- 選択された選択肢テーブル
CREATE TABLE attempt_selected_choices (
    attempt_id INT REFERENCES quiz_attempts(id),
    choice_id INT
);

-- 管理者ユーザーテーブル
CREATE TABLE admin_users (
    id SERIAL PRIMARY KEY,
    username VARCHAR NOT NULL UNIQUE,
    password_hash TEXT NOT NULL
);
```

