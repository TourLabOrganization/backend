# 인프라 구성

배포 파이프라인(GitHub Actions)은 **앱 컨테이너만** 교체한다. DB와 서버 초기
구성은 여기 적힌 순서로 **한 번만** 수동으로 한다. 자동화하면 배포 실수 한 번에
데이터가 날아갈 수 있다.

## 인스턴스

| 역할 | 사설 IP | 유형 | 비고 |
| --- | --- | --- | --- |
| API (Spring) | — | t4g.small (arm64) | nginx · certbot 동거 |
| DB (PostgreSQL) | 172.31.3.171 | t4g.small (arm64) | 외부 노출 없음 |

## DB 인스턴스 초기 구성

```bash
sudo yum update -y && sudo yum install -y docker
sudo systemctl enable --now docker
sudo usermod -aG docker ec2-user
exit    # 그룹 권한은 재로그인해야 적용된다
```

```bash
docker run -d --name tourlab-db --restart unless-stopped \
  -e POSTGRES_DB=tourlab \
  -e POSTGRES_USER=tourlab \
  -e POSTGRES_PASSWORD='<API 서버 app.env 의 DB_PASSWORD 와 동일>' \
  -p 5432:5432 -v pgdata:/var/lib/postgresql/data \
  postgres:16
```

**이미지는 `postgres:16`을 쓴다.** `postgis/postgis`는 arm64 빌드가 없어
Graviton 인스턴스에서 무한 재시작한다. 좌표 검색은 현재 규모(장소 1,171곳)에서
일반 쿼리로 충분하다.

## 보안 그룹

DB 인스턴스는 인터넷에 포트를 열지 않는다. 5432만 API 서버에서 허용한다.

```
유형   PostgreSQL (5432)
소스   <API 서버의 보안 그룹 ID>      ← 0.0.0.0/0 금지
```

확인:

```bash
# API 서버에서
nc -zv 172.31.3.171 5432
```

## 스키마와 데이터

테이블은 앱이 뜰 때 JPA가 만든다(`ddl-auto: update`). 수동 생성 불필요.
운영 단계로 가면 Flyway 같은 마이그레이션 도구로 교체해야 한다.

## 비밀번호

`app.env`(API 서버 `~/app/app.env`)에만 둔다. 이 문서와 저장소에는 적지 않는다.
