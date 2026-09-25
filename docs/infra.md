# 인프라 구성

배포 파이프라인(GitHub Actions)은 **앱 컨테이너만** 교체한다. DB와 서버 초기
구성은 여기 적힌 순서로 **한 번만** 수동으로 한다. 자동화하면 배포 실수 한 번에
데이터가 날아갈 수 있다.

## 인스턴스

| 역할 | 사설 IP | 유형 | 비고 |
| --- | --- | --- | --- |
| API (Spring) | 172.31.13.185 | t4g.small (arm64) | nginx · certbot 동거 |
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

## 도메인과 인증서

공인 인증서는 **IP 주소로는 발급되지 않는다**(Let's Encrypt 정책). 그래서 IP를
그대로 이름으로 되돌려주는 무료 와일드카드 DNS `nip.io`를 쓴다.

```
DOMAIN=3.36.114.238.nip.io      →  3.36.114.238
```

`app.env`의 `DOMAIN`이 nginx 템플릿과 인증서 경로 양쪽에 그대로 들어가므로,
**IP가 바뀌면 여기만 고치고 인증서를 다시 발급**하면 된다. 정식 도메인을 사면
같은 자리에 도메인만 넣는다.

### 최초 발급

nginx는 인증서가 없으면 뜨지 못하고, webroot 검증은 nginx가 떠 있어야 한다.
그래서 **첫 발급만** nginx를 내리고 standalone으로 받는다.

```bash
cd ~/app
docker stop app-nginx app-certbot

docker run --rm -p 80:80 \
  -v app_certbot-conf:/etc/letsencrypt \
  -v app_certbot-www:/var/www/certbot \
  certbot/certbot:v5.7.0 certonly --standalone \
  -d "$DOMAIN" --email <관리자 메일> --agree-tos --no-eff-email -n
```

### 갱신은 webroot로

발급 직후 갱신 설정은 `standalone`으로 저장돼 있는데, 이대로 두면 **갱신이
반드시 실패한다** — 80 포트를 nginx가 잡고 있기 때문이다. nginx가
`/.well-known/acme-challenge/`를 이미 서빙하므로 webroot로 바꾼다.

`/etc/letsencrypt/renewal/<DOMAIN>.conf` 의 `[renewalparams]` 안이어야 한다.
섹션을 잘못 잡으면 값이 무시된다.

```ini
[renewalparams]
authenticator = webroot
webroot_path = /var/www/certbot,
[[webroot_map]]
<DOMAIN> = /var/www/certbot
```

확인:

```bash
docker exec app-certbot certbot renew --dry-run
```

`certbot` 컨테이너가 12시간마다 `certbot renew`를 돌린다.


## 스키마와 데이터

테이블은 앱이 뜰 때 JPA가 만든다(`ddl-auto: update`). 수동 생성 불필요.
운영 단계로 가면 Flyway 같은 마이그레이션 도구로 교체해야 한다.

## 비밀번호

`app.env`(API 서버 `~/app/app.env`)에만 둔다. 이 문서와 저장소에는 적지 않는다.
