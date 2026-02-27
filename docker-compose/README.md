# Redis Practice Repository

강의 실습용 Redis 환경을 Docker Compose로 실행합니다.

## 1. Redis 실행

``` bash
docker compose up -d
```

## 2. 실행 확인

``` bash
docker compose ps
```

## 3. Redis 접속 (CLI)

``` bash
docker exec -it redis-stack-compose redis-cli -a systempass
```

※ 비밀번호는 docker-compose.yaml의 `REDIS_ARGS` 값을 확인하세요.
