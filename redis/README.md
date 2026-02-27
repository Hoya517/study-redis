# Spring Boot + Redis Practice

Spring Boot 환경에서 Redis를 연동하고, Spring Data Repository와
RedisTemplate을 이용한 CRUD를 실습한 프로젝트입니다.

------------------------------------------------------------------------

## 📌 학습 목표

-   Spring Boot에서 Redis 연결
-   Spring Data Redis Repository 사용
-   RedisTemplate을 이용한 자료형 직접 제어
-   Redis 직렬화 / 역직렬화 이해

------------------------------------------------------------------------

## 🛠 Tech Stack

-   Java 17
-   Spring Boot 3.x
-   Spring Data Redis
-   Redis Stack (Docker)

------------------------------------------------------------------------

## 🚀 Redis 실행

``` bash
docker compose up -d
```

Redis 기본 포트: localhost:6379

------------------------------------------------------------------------

## 1️⃣ Spring Data Repository 사용

-   `@RedisHash` 적용
-   `CrudRepository` 상속
-   Hash 자료형으로 자동 저장
-   `_class` 메타데이터 포함 저장
-   기본 CRUD 테스트 작성

### 특징

-   JPA와 유사한 방식으로 사용 가능
-   단순 CRUD에 적합

------------------------------------------------------------------------

## 2️⃣ RedisTemplate 사용

-   `StringRedisTemplate`
-   `opsForValue()`
-   `opsForSet()`
-   `EXPIRE`, `DEL` 등 직접 제어
-   `@Configuration`에서 커스텀 RedisTemplate 정의

### 특징

-   자료형 직접 선택 가능
-   복잡한 Redis 기능 구현에 적합

------------------------------------------------------------------------

## 💡 정리

| 방식 | 장점 | 한계 |
|------|------|------|
| Repository | 간단한 CRUD, JPA와 유사 | 자료형 제어 한계 |
| RedisTemplate | 자료형 직접 제어 가능 | 코드량 증가 |
