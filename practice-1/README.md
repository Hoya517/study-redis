# Practice 1 - Spring Data Redis (RedisHash + CRUD)

## 📌 목표

Spring Boot에서 Redis를 연동하여 `@RedisHash` 기반으로 주문 데이터를
저장하고 CRUD를 구현한다.

------------------------------------------------------------------------

## 🛠 Tech Stack

-   Java 17
-   Spring Boot 3.x
-   Spring Data Redis
-   Lettuce
-   Redis (Docker)

------------------------------------------------------------------------

## 📂 주요 구현 내용

### 1️⃣ ItemOrder RedisHash 생성

Redis에 저장될 도메인 객체:

-   주문 ID (String)
-   판매 물품 (String)
-   갯수 (Integer)
-   총액 (Long)
-   주문 상태 (String)

`@RedisHash`를 이용하여 Hash 자료형으로 저장

------------------------------------------------------------------------

### 2️⃣ CRUD 기능 구현

-   Create
-   Read
-   Update
-   Delete

✔ ID를 제외한 필드는 클라이언트 요청으로 전달\
✔ 저장 성공 시 저장된 `ItemOrder` 반환

------------------------------------------------------------------------

## 📌 학습 포인트

-   Spring Data Redis Repository 사용 방법
-   Redis Hash 구조 이해
-   JPA와의 유사점 이해

------------------------------------------------------------------------

## 📚 요약

Spring Data Repository를 이용하면 Redis CRUD를 JPA처럼 간단하게 구현할 수 있다.
