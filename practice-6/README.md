
# Redis Caching Practice - Leaderboard & Write-Behind

## 📌 프로젝트 개요

이 프로젝트는 Redis를 활용한 **고성능 캐싱 전략**을 실습하기 위한 예제입니다.  
특가 판매, 라이브 커머스 등 **짧은 시간 동안 주문이 폭증하는 상황**을 가정하고 다음 두 가지 기능을 구현합니다.

1. **구매 랭킹(Leaderboard)** — Redis Sorted Set 기반
2. **Write-Behind 캐싱** — Redis → 일정 주기 DB 반영

이 프로젝트를 통해 **Redis를 단순 캐시가 아닌 데이터 처리 계층으로 활용하는 방법**을 학습합니다.

---

# 🧠 실습 목표

- Redis Sorted Set을 이용한 **상품 구매 랭킹**
- Redis 기반 **실시간 주문 저장**
- **Write-Behind 캐싱 전략 구현**
- Redis → DB **비동기 데이터 반영 구조 이해**

---

# 🛠 Tech Stack

| 기술 | 설명 |
|-----|-----|
| Java 17 | Backend Language |
| Spring Boot | API 서버 |
| Spring Data JPA | DB 접근 |
| Redis | 캐싱 및 랭킹 저장 |
| Redis Sorted Set | Leaderboard |
| Scheduler | Write-Behind DB 반영 |
| Docker Redis | Redis 실행 환경 |

---

# 📦 주요 도메인

## Item

| 필드 | 타입 |
|-----|-----|
| id | Long |
| name | String |
| price | Long |

## ItemOrder

| 필드 | 타입 |
|-----|-----|
| id | Long |
| itemId | Long |
| quantity | Integer |

---

# 🚀 Redis Leaderboard 구현

구매가 발생할 때마다 Redis Sorted Set에 점수를 증가시켜 **구매 랭킹**을 관리합니다.

## 저장 방식

```
key: item:ranking
type: Sorted Set
score: 구매 수량
value: itemId
```

### 구매 발생 시

```
ZINCRBY item:ranking {quantity} {itemId}
```

예시

```
ZINCRBY item:ranking 3 item:1
ZINCRBY item:ranking 1 item:5
```

---

# 🏆 인기 상품 TOP 10 조회

```
ZREVRANGE item:ranking 0 9 WITHSCORES
```

설명

- 점수가 높은 순으로 조회
- 상위 10개 상품 반환

---

# ⚡ Write-Behind 캐싱

대량 주문 상황에서는 DB에 직접 쓰기 작업을 수행하면 **DB 병목이 발생**합니다.

이를 해결하기 위해 다음 전략을 사용합니다.

### 흐름

```
Client
   ↓
API Server
   ↓
Redis 저장 (즉시)
   ↓
주기적 배치 작업
   ↓
DB 반영
```

즉,

**쓰기 요청은 Redis에 먼저 저장하고**
**일정 주기로 DB에 반영합니다.**

---

# 📊 Redis 데이터 구조

| Key | Type | 설명 |
|----|----|----|
| item:ranking | Sorted Set | 상품 구매 랭킹 |
| item:orders | List / Hash | 주문 임시 저장 |
| item:orders:buffer | Queue | Write-Behind 처리 대상 |

---

# ⏱ Write-Behind 처리

Spring Scheduler를 이용하여 일정 주기로 Redis 데이터를 DB로 옮깁니다.

예시 흐름

```
1️⃣ Redis에서 주문 데이터 조회
2️⃣ DB에 Batch Insert
3️⃣ Redis 데이터 삭제
```

예시 코드 흐름

```java
@Scheduled(fixedDelay = 5000)
public void flushOrdersToDatabase() {
    // Redis → DB 저장
}
```

---

# 📈 캐싱 전략 비교

| 전략 | 특징 |
|----|----|
| Cache Aside | 조회 시 캐시 사용 |
| Write Through | DB와 캐시 동시 저장 |
| Write Behind | 캐시 먼저 저장 후 DB 반영 |

이 프로젝트는 **Write-Behind 전략**을 실습합니다.

---

# 🎯 기대 효과

| 효과 |
|----|
| 대량 트래픽 대응 |
| DB 부하 감소 |
| 빠른 랭킹 계산 |
| 주문 처리 성능 향상 |

---

# ⚠️ 실무 고려 사항

실무에서는 다음 요소를 추가로 고려해야 합니다.

- Redis 장애 대응
- 데이터 유실 방지
- Redis Persistence 설정 (AOF / RDB)
- 주문 데이터 정합성
- Write-Behind 실패 재처리
- Distributed Lock

---

# 📚 핵심 학습 포인트

- Redis Sorted Set Leaderboard 구현
- Redis 기반 고성능 캐싱 전략
- Write-Behind 아키텍처 이해
- Redis를 데이터 처리 계층으로 활용

---

# 🧾 정리

이 실습 프로젝트는 단순 캐시가 아니라 **대용량 트래픽 상황에서 Redis를 활용한 시스템 설계**를 학습하는 것을 목표로 합니다.

핵심은 다음입니다.

- Redis Sorted Set → 랭킹 처리
- Redis → 주문 임시 저장
- Scheduler → DB 반영
- Write-Behind 캐싱 전략 적용
