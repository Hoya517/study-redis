
# Redis Study

Spring Boot 환경에서 Redis를 다양한 방식으로 활용하는 방법을 실습하며 학습한 프로젝트입니다.

이 저장소에서는 Redis의 기본 자료구조부터 시작하여 다음과 같은 실무 활용 패턴을 단계적으로 학습합니다.

- Redis 기본 자료구조
- Spring Boot + Redis 연동
- RedisTemplate 활용
- Redis Leaderboard 구현
- Spring Cache 적용
- Session Clustering
- Write-Behind 캐싱 전략

---

# Tech Stack

- Java 17
- Spring Boot
- Spring Data Redis
- Spring Cache
- Spring Session
- Redis
- Docker

---

# Practice List

| Project | Topic | Description |
|-------|------|-------------|
| practice-1 | Redis Repository CRUD | Spring Data Redis Repository 기반 CRUD 실습 |
| practice-2 | RedisTemplate | RedisTemplate을 이용한 직접 Redis 자료구조 제어 |
| practice-3 | Redis Cart + TTL | Redis Hash 기반 장바구니 구현 및 TTL 관리 |
| practice-4 | Session Clustering | Spring Session + Redis 기반 세션 공유 |
| practice-5 | Spring Cache | Spring Cache + Redis를 이용한 조회 캐싱 |
| practice-6 | Leaderboard + Write Behind | Sorted Set 기반 랭킹 및 Write-Behind 캐싱 전략 구현 |

---

# Redis Basic Data Structures

Redis CLI를 사용하여 기본 자료구조를 학습합니다.

- String
- Set
- Hash
- Sorted Set

예시

```
INCR article:1
SADD article:viewers user1
ZINCRBY ranking 1 item1
```

---

# Spring Boot + Redis

Spring Boot 프로젝트에서 Redis를 연결하여 데이터를 저장하고 조회합니다.

사용 기술

- RedisTemplate
- StringRedisTemplate
- Spring Data Redis Repository

학습 내용

- Redis 직렬화 / 역직렬화
- Redis 데이터 구조 설계
- Spring Boot에서 Redis 사용 방법

---

# Redis Leaderboard

Redis Sorted Set을 이용한 랭킹 시스템 구현

```
ZINCRBY item:ranking 1 item:1
ZREVRANGE item:ranking 0 9 WITHSCORES
```

특징

- 자동 정렬
- 빠른 조회 성능
- 실시간 랭킹 계산

---

# Redis Caching

Spring Cache와 Redis를 이용하여 조회 성능을 개선합니다.

사용 Annotation

- `@Cacheable`
- `@CachePut`
- `@CacheEvict`

적용 대상

- 단건 조회 캐싱
- 목록 조회 캐싱
- 수정 시 캐시 갱신
- 삭제 시 캐시 무효화

---

# Session Clustering

Spring Session + Redis를 사용하여 여러 서버 인스턴스 간 세션을 공유합니다.

구조

Client  
↓  
Load Balancer  
↓  
Application Servers  
↓  
Redis Session Store  

특징

- 로그인 세션 공유
- Stateless 서버 구성 가능

---

# Write-Behind Caching

대량 주문 상황에서 DB 부하를 줄이기 위한 전략입니다.

흐름

Client  
↓  
Application  
↓  
Redis 저장  
↓  
Scheduler  
↓  
DB 저장  

특징

- DB Write 부하 감소
- 높은 처리량 확보

---

# Redis Data Structures Summary

| Type | Use Case |
|-----|-----|
| String | 조회수 증가 |
| Hash | 장바구니 |
| Set | 사용자 집합 |
| Sorted Set | Leaderboard |
| List | Queue / Buffer |

---

# Learning Outcome

이 저장소는 Redis를 단순 캐시가 아니라

**고성능 데이터 처리 플랫폼으로 활용하는 방법**을 학습하는 것을 목표로 합니다.

핵심 학습 내용

- Redis 자료구조 활용
- Redis + Spring Boot 통합
- 캐싱 전략 설계
- 대규모 트래픽 대응 아키텍처
