# 🚀 Spring Boot + Redis 캐싱 적용 실습

## 📌 학습 목표

-   Spring Cache 추상화 이해
-   RedisCacheManager 설정 및 적용
-   @Cacheable, @CachePut, @CacheEvict 사용법 학습
-   Cache-Aside / Write-Through 전략 이해
-   검색 결과 캐싱 적용

------------------------------------------------------------------------

# 1️⃣ 캐싱 개념

## 🔎 캐싱이란?

자주 조회되는 데이터를 메모리 기반 저장소(Redis)에 저장하여\
DB 접근 비용을 줄이는 기술.

## 📌 주요 용어

  용어              설명
  ----------------- -----------------------------
  Cache Hit         캐시에 데이터 존재
  Cache Miss        캐시에 데이터 없음
  TTL               캐시 유지 시간
  Eviction Policy   캐시 공간 부족 시 삭제 정책

------------------------------------------------------------------------

# 2️⃣ 캐싱 전략

## Cache-Aside

-   조회 시 캐시 먼저 확인
-   없으면 DB 조회 후 캐시에 저장
-   최초 요청은 느릴 수 있음

## Write-Through

-   저장 시 캐시 + DB 동시 저장
-   항상 최신 데이터 유지

## Write-Behind

-   캐시에 먼저 저장 후 일정 주기로 DB 반영
-   쓰기 성능 우수, 장애 시 데이터 유실 위험

------------------------------------------------------------------------

# 3️⃣ Spring Boot 캐싱 설정

## EnableCaching 설정

``` java
@Configuration
@EnableCaching
public class CacheConfig {
}
```

## RedisCacheManager 설정

``` java
@Bean
public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {

    RedisCacheConfiguration configuration =
            RedisCacheConfiguration.defaultCacheConfig()
                    .disableCachingNullValues()
                    .entryTtl(Duration.ofSeconds(10))
                    .computePrefixWith(CacheKeyPrefix.simple())
                    .serializeValuesWith(
                        SerializationPair.fromSerializer(
                            RedisSerializer.java()
                        )
                    );

    return RedisCacheManager.builder(redisConnectionFactory)
            .cacheDefaults(configuration)
            .build();
}
```

------------------------------------------------------------------------

# 4️⃣ 어노테이션 적용

## @Cacheable (조회)

``` java
@Cacheable(cacheNames = "itemCache", key = "args[0]")
public ItemDto readOne(Long id) { ... }
```

## @CachePut (생성)

``` java
@CachePut(cacheNames = "itemCache", key = "#result.id")
public ItemDto create(ItemDto dto) { ... }
```

## @CacheEvict (수정)

``` java
@CachePut(cacheNames = "itemCache", key = "args[0]")
@CacheEvict(cacheNames = "itemAllCache", allEntries = true)
public ItemDto update(Long id, ItemDto dto) { ... }
```

------------------------------------------------------------------------

# 5️⃣ 실습 정리

  구분        적용 전략
  ----------- -----------------------
  단건 조회   Cache-Aside
  생성        Write-Through
  수정        Write-Through + Evict
  전체 조회   Cache-Aside

------------------------------------------------------------------------

# 📈 기대 효과

-   DB 조회 감소
-   응답 속도 개선
-   서버 부하 감소
-   확장성 향상
