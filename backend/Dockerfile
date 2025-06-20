# Java 21（Eclipse Temurinディストリビューション）を使用
FROM eclipse-temurin:21-jdk

# 作業ディレクトリを /app に設定
WORKDIR /app

# プロジェクト全体をコピー（pom.xml, src/, mvnwなど）
COPY . /app

# Maven Wrapperを使ってビルド（テストはスキップ）
RUN ./mvnw clean package -DskipTests

# Spring Boot アプリケーションが使うポート
EXPOSE 8080

# アプリの起動コマンド
CMD ["java", "-jar", "target/quizapp-0.0.1-SNAPSHOT.jar"]