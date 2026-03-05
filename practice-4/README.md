# Practice 4 — Spring Session Clustering (Spring Security Form Login)

Spring Security **Form Login**을 적용하고, 인증 세션을 **Redis(Spring Session)** 에 저장하여  
여러 애플리케이션 인스턴스(멀티 인스턴스) 간에도 로그인 상태가 **공유(클러스터링)** 되는 것을 확인하는 실습 프로젝트입니다.

---

## What you build

- Spring Security **Form Login**
- **CSRF 비활성화**(실습 편의 목적)
- 사용자 저장소: `InMemoryUserDetailsManager`
- 세션 저장소: **Spring Session Data Redis**
- (기본) 세션 직렬화: **Spring Session 기본 설정(권장)**

---

## Tech Stack

- Java 17
- Spring Boot 3.x
- Spring Security
- Spring Session (spring-session-data-redis)
- Redis (or Redis Stack)

---

## Redis 준비

`application.yaml` 기준으로 Redis 접속 정보는 다음과 같습니다.

- Host: `localhost`
- Port: `6379`
- Username: `default`
- Password: `systempass`

> 패스워드를 쓰지 않는 Redis라면 `spring.data.redis.password` 설정을 제거하거나 환경변수로 덮어쓰세요.

### (옵션) redis-cli로 접속 확인

```bash
redis-cli -h localhost -p 6379 -a systempass
PING
# PONG
```

---

## Run

```bash
./gradlew bootRun
```

기본 포트는 `8080` 입니다.

---

## Login Flow

이 프로젝트는 커스텀 로그인 페이지를 사용합니다.

- 로그인 페이지: `GET /auth/login-form`
- 로그인 처리: `POST /auth/login`  
  - Spring Security가 처리 (컨트롤러가 직접 처리하지 않음)
- 로그인 성공 후 이동: `GET /auth/my-profile`

### 테스트용 계정

실습 코드는 `InMemoryUserDetailsManager`를 사용합니다.  
계정 정보는 `SecurityConfig`에서 확인/수정할 수 있습니다.

---

## 세션 클러스터링(공유) 확인 방법

목표: **서로 다른 애플리케이션 인스턴스**에서 **같은 로그인 세션**을 공유하는지 확인

### 1) 인스턴스 2개 띄우기

- 인스턴스 A: `8080`
- 인스턴스 B: `8081` (서버 포트만 변경)

예시(인스턴스 B 실행):

```bash
./gradlew bootRun --args='--server.port=8081'
```

### 2) 로그인 후, 다른 포트로 이동

1. `http://localhost:8080/auth/login-form` 에서 로그인
2. 로그인 성공 후 `http://localhost:8081/auth/my-profile` 접속
3. **로그인 상태가 유지되면 세션 클러스터링 성공**

> 쿠키는 “도메인/경로” 기준이므로, 포트가 달라도 동일 도메인(localhost)에서는 같은 세션 쿠키가 전달됩니다.

---

## Redis에 세션이 저장되는지 확인

로그인 후 `redis-cli`에서 세션 키를 확인할 수 있습니다(키 패턴은 환경에 따라 다를 수 있음).

```bash
# 예: 세션 관련 키 조회
KEYS spring:session:*
```

---

## 프로젝트 구조 (핵심)

- `SecurityConfig`
  - Form Login 설정
  - CSRF 비활성화(실습용)
  - `InMemoryUserDetailsManager` 사용자 구성
- `RedisConfig`
  - `LettuceConnectionFactory` 기반 Redis 연결
  - (옵션) Redis serializer 설정
- `AuthController`
  - 로그인 폼 / 프로필 화면 매핑
- `SessionController`
  - 세션 객체를 조회해 화면에서 확인(디버깅/학습용)

---

## 오류 및 한계점 (JSON serializer 적용 시)

### 재현 조건

`RedisConfig`에서 아래 Bean을 **주석 해제**하고 실행 후, `GET /auth/login` 또는 `GET /auth/login-form` 등 세션을 읽는 요청을 보내면 오류가 발생할 수 있습니다.

```java
@Bean
public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
    return RedisSerializer.json();
}
```

### 오류 요약

- 증상: `org.springframework.data.redis.serializer.SerializationException: Could not read JSON`
- 로그 예시: `Unexpected character ('¬' ...)` 로 시작하는 **JSON 파싱 실패**
- 의미:
  - Redis에 들어있는 세션 값이 **JSON이 아닌 다른 형식**(예: 기존 JDK 직렬화 등)으로 저장되어 있거나,
  - 세션 attribute(특히 `SPRING_SECURITY_CONTEXT`)에 저장되는 객체들이 **JSON 직렬화/역직렬화에 적합하지 않아** 파싱/복원이 깨진 상태입니다.

### 한계점 정리

- Spring Security는 세션에 `SPRING_SECURITY_CONTEXT`(SecurityContext, Authentication 등) 같은 **복잡한 객체 그래프**를 저장합니다.
- 이를 `RedisSerializer.json()`(Jackson 기반)으로 “그대로” 저장/복원하려면:
  - SecurityContext 및 관련 클래스들의 **직렬화/역직렬화 전략을 직접 보장**해야 합니다.
  - 즉, **SecurityContext를 직접 serialize/deserialize 하도록 구현**하거나,
  - 혹은 **SecurityContext를 직접 구성해 사용할 수 있도록 필터 체인/저장 로직을 직접 구성**하지 않는 이상,
  - 현재 상태에서는 **JSON serializer로는 그대로 사용하기 어렵다**고 보는 게 안전합니다.

> 결론: 이 실습 프로젝트에서는 (학습 목적상) **Spring Session 기본 직렬화 방식으로 동작시키는 것을 권장**합니다.  
> JSON serializer로 전환을 실험한다면, 세션 데이터 정리(예: Redis flush) 및 직렬화 호환성 확보가 선행되어야 합니다.

---

## Reference

- Spring Security Form Login 문서: https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/form.html
