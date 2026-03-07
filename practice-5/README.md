# 🏪 Practice 5 - Redis를 이용한 캐싱 적용해보기

## 📌 프로젝트 개요

상품을 판매하는 **Store** 도메인을 대상으로 CRUD를 구현하고,
조회 성능 개선과 데이터베이스 부하 감소를 위해 **Redis 기반 캐싱**을 적용한 실습 프로젝트입니다.

이 프로젝트를 통해 다음을 학습합니다.

- JPA Entity 기반 Store CRUD 구현
- Spring Cache + Redis 적용
- `@Cacheable`, `@CachePut`, `@CacheEvict` 활용
- 조회/수정/목록 기능별 캐시 전략 설계

---

## ✅ 요구사항

### Store Entity

`Store`는 JPA Entity이며 다음 속성을 가집니다.

- 스토어 ID - `Long`
- 이름 - `String`
- 분류 - `String`
  예: 패션, 디지털 등

### 구현 목표

- Store CRUD 구현
- 필요한 지점에 캐싱 적용
- 어떤 기능에 어떤 캐싱 전략을 썼는지 정리

---

## 🛠 Tech Stack

- Java 17
- Spring Boot 3.x
- Spring Data JPA
- Spring Cache
- Spring Data Redis
- Redis (Docker)
- H2 / MySQL 등 JPA 저장소

---

## 🚀 Redis 실행

```bash
docker compose up -d
```

기본 포트:

- Redis: `localhost:6379`

---

## 🧱 도메인 구조

### Store

| 필드 | 타입 | 설명 |
|------|------|------|
| id | Long | 스토어 식별자 |
| name | String | 스토어 이름 |
| category | String | 스토어 분류 |

---

## 📦 구현 기능

- Store 생성
- Store 단건 조회
- Store 전체 조회
- Store 수정
- Store 삭제

---

## ⚡ 캐싱 적용 전략

이 프로젝트에서는 조회 성능 개선이 핵심이므로
읽기 기능에 우선적으로 캐시를 적용하고,
데이터 변경 시 캐시를 갱신 또는 제거하도록 구성합니다.

### 1) 단건 조회 캐싱

예시:

```java
@Cacheable(cacheNames = "storeCache", key = "args[0]")
public StoreDto readOne(Long id) { ... }
```

#### 이유
- 특정 Store 조회는 반복 호출 가능성이 높음
- 동일 ID에 대한 반복 조회 시 DB 접근을 줄일 수 있음

#### 적용 전략
- **Cache-Aside**
- 최초 조회 시 DB 조회 후 캐시 저장
- 이후 동일 요청은 캐시 반환

---

### 2) 전체 조회 캐싱

예시:

```java
@Cacheable(cacheNames = "storeAllCache", key = "'all'")
public List<StoreDto> readAll() { ... }
```

#### 이유
- 전체 목록 조회는 자주 호출될 수 있음
- 변경 빈도보다 조회 빈도가 높다면 캐싱 효과가 큼

#### 적용 전략
- **Cache-Aside**
- 단, 데이터 변경 시 전체 목록 캐시는 무효화 필요

---

### 3) 생성 후 캐시 반영

예시:

```java
@CachePut(cacheNames = "storeCache", key = "#result.id")
public StoreDto create(StoreDto dto) { ... }
```

#### 이유
- 생성 직후 같은 데이터를 조회할 가능성이 있음
- 생성된 결과를 바로 캐시에 반영하면 이후 조회가 빨라짐

#### 적용 전략
- **Write-Through 유사 방식**
- 메서드는 항상 실행되고, 결과를 캐시에 저장

---

### 4) 수정 시 캐시 갱신 + 목록 캐시 무효화

예시:

```java
@CachePut(cacheNames = "storeCache", key = "args[0]")
@CacheEvict(cacheNames = "storeAllCache", allEntries = true)
public StoreDto update(Long id, StoreDto dto) { ... }
```

#### 이유
- 단건 캐시는 수정된 최신값으로 갱신 필요
- 전체 목록 캐시는 더 이상 유효하지 않을 수 있음

#### 적용 전략
- 단건: **갱신**
- 목록: **무효화**

---

### 5) 삭제 시 캐시 제거

예시:

```java
@CacheEvict(cacheNames = "storeCache", key = "args[0]")
@CacheEvict(cacheNames = "storeAllCache", allEntries = true)
public void delete(Long id) { ... }
```

#### 이유
- 삭제된 데이터가 캐시에 남아 있으면 잘못된 조회 결과 발생
- 전체 목록에서도 제거되어야 하므로 목록 캐시도 무효화 필요

---

## 🧠 캐싱 적용 이유 정리

| 기능 | 적용 방식 | 이유 |
|------|-----------|------|
| 단건 조회 | `@Cacheable` | 반복 조회 최적화 |
| 전체 조회 | `@Cacheable` | 목록 조회 성능 개선 |
| 생성 | `@CachePut` | 생성 직후 캐시 반영 |
| 수정 | `@CachePut` + `@CacheEvict` | 최신 데이터 반영 + 목록 무효화 |
| 삭제 | `@CacheEvict` | 삭제된 데이터 캐시 제거 |

---

## 📚 캐시 전략 관점 정리

| 전략 | 사용 지점 | 설명 |
|------|-----------|------|
| Cache-Aside | 단건 조회, 전체 조회 | 조회 시 캐시 먼저 확인 |
| Write-Through 유사 | 생성, 수정 | 변경 결과를 즉시 캐시에 반영 |
| Eviction | 수정, 삭제 | 더 이상 유효하지 않은 캐시 제거 |

---

## 🎯 기대 효과

- 반복 조회 시 응답 속도 향상
- 데이터베이스 부하 감소
- 조회 중심 API 성능 최적화
- CRUD와 캐시 일관성 관리 방식 학습

---

## ⚠️ 실무에서 추가로 고려할 점

- TTL 설정
- 캐시 키 네이밍 전략
- 캐시 무효화 범위 최소화
- 캐시 스탬피드 방지
- 분산 환경에서의 캐시 정합성

---

## 📝 결론

이 프로젝트는 단순 CRUD 구현을 넘어,
**어떤 기능에 캐싱을 적용해야 하는지**,
그리고 **왜 그런 전략을 선택해야 하는지**를 학습하는 실습입니다.

핵심은 다음과 같습니다.

- 조회는 캐시로 빠르게
- 변경은 캐시 정합성을 고려해 갱신 또는 제거
- 캐시는 “무조건”이 아니라 “적절한 지점”에 적용해야 효과적
