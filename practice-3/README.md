# 🛒 Practice 3 - Redis Hash 기반 장바구니 (세션 클러스터링 기초)

## 📌 프로젝트 개요

로그인 없이도 사용할 수 있는 **장바구니 기능**을 구현합니다.

- 장바구니 데이터는 **Redis Hash**로 관리
- 사용자 식별은 **HttpSession ID(쿠키 기반)** 로 처리
- 장바구니가 일정 시간 사용되지 않으면 **TTL로 자동 삭제**
- 여러 애플리케이션 인스턴스에 부하가 분산되어도(로드밸런싱) 동일 장바구니를 조회/수정할 수 있도록, 상태를 Redis에 저장

---

## ✅ 학습 목표

- Redis **Hash**로 장바구니(아이템 수량) 모델링
- `HINCRBY` 기반 수량 증감(음수 포함) 처리
- 수량이 **0 이하가 되면 자동 제거**
- 장바구니 Key에 **TTL(만료시간) 적용** 및 갱신
- (개념) 다중 인스턴스 환경에서 세션 기반 상태 공유

---

## 🛠 Tech Stack

- Java 17
- Spring Boot 3.x
- Spring Data Redis (Lettuce)
- Redis (Docker)

---

## 🚀 Redis 실행

```bash
docker compose up -d
```

기본 포트: `localhost:6379`

---

## 🧠 Redis 설계

### Key 규칙

- `cart:{sessionId}`  
  - `sessionId`는 `HttpSession#getId()` 값

### 자료 구조

- **Hash**
  - Field: `itemId` (String)
  - Value: `count` (Integer)

예시:

```text
HGETALL cart:abc123
1 -> 3
2 -> 1
```

---

## 🔁 동작 규칙

| 기능 | Redis 명령 | 설명 |
|------|-----------|------|
| 수량 증감 | `HINCRBY cart:{sessionId} {itemId} {delta}` | `delta`는 양수/음수 모두 가능 |
| 장바구니 조회 | `HGETALL cart:{sessionId}` | 현재 장바구니 전체 조회 |
| 아이템 제거 | `HDEL cart:{sessionId} {itemId}` | 수량이 0 이하가 되면 제거 |
| TTL 적용 | `EXPIRE/EXPIREAT cart:{sessionId}` | 장바구니 Key 자체를 만료 처리 |
| TTL 갱신 | 매 요청마다 TTL 재설정 | “사용 중이면 TTL 연장” 전략 |

> 강의 요구사항은 **3시간 후 삭제**입니다.  
> 해설 코드에서는 테스트 편의를 위해 TTL이 더 짧게(예: 30초) 설정되어 있을 수 있으니, 실습 시 `3 hours`로 변경하세요.

---

## 📡 API (예시)

> 해설 코드 기준으로 **세션(쿠키)** 를 사용합니다.  
> Postman에서 같은 쿠키를 유지하면 동일 장바구니를 계속 조회/수정할 수 있습니다.

### 1) 장바구니 수량 조정

- `POST /cart/items/{itemId}`
- Body (예시):

```json
{ "delta": 1 }
```

- `delta`가 음수면 수량 감소
- 결과 수량이 0 이하가 되면 해당 아이템 제거

### 2) 장바구니 조회

- `GET /cart`

---

## 🔧 구현 포인트

### 1) 세션 ID 기반 사용자 구분

- 별도 User 엔티티 없이 `HttpSession`으로 사용자 식별
- `sessionId`를 Redis Key에 포함

### 2) TTL 전략

- 장바구니에 접근(조회/수정)할 때마다 TTL을 갱신하여, “최근 사용 기준”으로 만료 처리

### 3) 수량 0 이하 제거

- `HINCRBY` 결과(누적 수량)가 0 이하가 되면 `HDEL`로 정리

---

## 🧪 검증 시나리오

1. `/cart/items/1`에 `delta=+2` → 아이템 1 수량 2
2. `/cart/items/1`에 `delta=-1` → 아이템 1 수량 1
3. `/cart/items/1`에 `delta=-1` → 수량 0 → 아이템 1 제거
4. 일정 시간(3시간) 미사용 → `cart:{sessionId}` Key 자동 삭제

---

## 💬 면접 대비 설명 포인트

- “왜 Hash인가?”  
  - 한 사용자 장바구니를 `cart:{user}`로 묶고, 필드로 아이템을 관리하면 조회/수정이 단순해짐.
- “왜 TTL을 Key에 걸었나?”  
  - 로그인 없는 장바구니는 영구 보관할 이유가 없고, 자동 정리를 통해 메모리/데이터 관리가 쉬움.
- “다중 인스턴스에서 어떻게 공유되나?”  
  - 서버 메모리가 아니라 Redis에 상태를 저장하므로, 어떤 인스턴스로 라우팅되어도 동일 상태를 읽을 수 있음.
