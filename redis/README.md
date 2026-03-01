# Spring Boot + Redis Practice

Spring Boot 환경에서 Redis를 연동하고,

-   Spring Data Repository
-   RedisTemplate
-   HttpSession 동작 방식
-   Redis 기반 Session Clustering (Spring Session)

을 실습한 프로젝트입니다.

------------------------------------------------------------------------

## 📌 학습 목표

-   Spring Boot에서 Redis 연결
-   Spring Data Redis Repository 사용
-   RedisTemplate을 이용한 자료형 직접 제어
-   Redis 직렬화 / 역직렬화 이해
-   HttpSession 동작 원리 이해
-   Scale-Out 환경에서 세션 문제 인식
-   Redis를 이용한 Session Clustering 구현

------------------------------------------------------------------------

## 🛠 Tech Stack

-   Java 17
-   Spring Boot 3.x
-   Spring Data Redis
-   Spring Session Data Redis
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
-   직렬화 방식 직접 설정 가능

------------------------------------------------------------------------

## 3️⃣ HttpSession 동작 원리

HTTP는 Stateless 프로토콜입니다.

-   서버는 기본적으로 이전 요청을 기억하지 않음
-   세션 유지를 위해 브라우저에 `JSESSIONID` 쿠키 발급
-   Tomcat이 내부 메모리에 세션 저장

### 문제 상황 (Scale-Out)

서버를 여러 대로 확장하면:

-   A 서버에 생성된 세션을
-   B 서버에서는 찾을 수 없음

즉, 서버가 달라지면 세션이 유지되지 않는 문제가 발생합니다.

------------------------------------------------------------------------

## 4️⃣ Sticky Session vs Session Clustering

### Sticky Session

-   특정 사용자를 특정 서버로 고정
-   구현은 쉬움
-   서버 다운 시 세션 손실
-   부하 분산이 균등하지 않을 수 있음

### Session Clustering

-   세션을 외부 저장소(Redis)에 저장
-   어떤 서버로 요청이 가도 세션 유지 가능
-   Scale-Out에 적합
-   Redis와 통신 비용 발생

------------------------------------------------------------------------

## 5️⃣ Spring Session + Redis 적용

의존성 추가:

``` gradle
implementation 'org.springframework.session:spring-session-data-redis'
```

적용 후:

-   Tomcat 내부 세션 대신 Redis에 세션 저장
-   `JSESSIONID` 대신 `SESSION` 쿠키 사용
-   여러 서버(8080, 8081) 실행 시에도 세션 유지 확인

### 장점

-   완전한 분산 환경 지원
-   서버 추가/제거 자유로움
-   실무 MSA 구조에서 필수 개념

------------------------------------------------------------------------

## 💡 최종 정리

구분                       저장 위치       확장성   단점
  -------------------------- --------------- -------- ------------------------
기본 HttpSession           Tomcat 메모리   낮음     서버 변경 시 세션 유실
Sticky Session             서버 고정       보통     부하 불균형 가능
Redis Session Clustering   Redis           높음     외부 통신 비용

------------------------------------------------------------------------

## 🎯 실무 관점 정리

-   단일 서버 환경 → 기본 HttpSession 가능
-   Scale-Out 환경 → Redis 기반 세션 관리 필수
-   Spring Session + Redis는 분산 세션 구현의 표준적인 방식
