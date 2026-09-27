# Tour Navigator Backend

영상 속 장소를 따라 걷는 여행 계획 앱의 API 서버.
장소·코스 데이터와 회원 인증을 맡고, 추천 계산은 별도 `data-server`(FastAPI)가 맡는다.

## 기술 스택

| 구분 | 사용 기술 |
|---|---|
| 언어 | Java 21 |
| 프레임워크 | Spring Boot 4.0.7 (Spring Framework 7) |
| 빌드 | Gradle 9.5.1 (wrapper) |
| 데이터베이스 | PostgreSQL 16 |
| API 문서 | springdoc-openapi 3.0.3 (Swagger UI) |
| 인증 | Spring Security 7 + JWT (oauth2 resource server) |
| 코드 포맷 | Spotless + google-java-format 1.22.0 |
| 배포 | GitHub Actions → GHCR → EC2 (arm64) + nginx + certbot |

## 저장소 구성

| 저장소 | 역할 |
|---|---|
| `backend` (이 저장소) | 장소·코스 API, 인증, 배포 |
| `frontend` | Next.js 웹앱 (Vercel) |
| `data-server` | 데이터랩·TourAPI 파이프라인과 추천 (FastAPI). 사설 IP라 이 서버만 호출한다 |
| `Tour-Navigator-App` | 기획 프로토타입 · 데이터랩 ETL |

## 들어 있는 것

| 영역 | 내용 |
|---|---|
| 장소·분석 | data-server 중계 5종. 계산은 data-server에서 끝나고 여기서는 넘겨주기만 한다 |
| 도메인 | `places` · `courses` · `course_places` 엔티티, `users` · `refresh_tokens` |
| 공통 응답 | `ApiResult` / `ErrorResult` / `SuccessCode` / `ErrorCode` |
| 예외 처리 | `ApiException` + `GlobalExceptionHandler` (12개 핸들러) |
| API 문서 | `SwaggerConfig` + `@ApiErrorCodeExample(s)` — 에러 코드 상수에서 응답 예시 생성 |
| 인증 | 이메일·비밀번호 로그인, JWT access/refresh 발급, refresh token 회전, 로그아웃 |
| 인가 | `Role`(USER/ADMIN) + `@EnableMethodSecurity`, 401/403 JSON 응답 핸들러 |
| JPA | `BaseTimeEntity` + `JpaAuditingConfig` |
| 헬스체크 | `GET /health` |
| 툴링 | Spotless, `lombok.config`(위험한 lombok 애노테이션 컴파일 차단), pre-commit hook |
| 테스트 | Testcontainers(PostgreSQL 16) 기반 컨텍스트 로드 + 인증 흐름 통합 테스트 |
| CI/CD | CI(포맷+빌드+테스트), CD(GHCR 이미지 → EC2 배포 → 헬스체크), Release(PR 제목 기반 태깅) |
| 컨벤션 | `CLAUDE.md` + `docs/` 9개 문서 (`AGENTS.md`는 `CLAUDE.md`를 가리키는 포인터) |

## 도메인 모델

```
Course ──< CoursePlace >── Place
```

- `Place` — 장소 그 자체. 이름·좌표·좌표 출처·지역·설명·운영시간·배지(유네스코/무장애/한국관광 100선).
- `Course` — 영상 한 편(또는 한 작품)을 따라 도는 코스.
- `CoursePlace` — 코스에 장소가 어떻게 들어가는지. 순번과 장면 설명은 코스마다 다르므로 여기에 둔다.
  `sequence`가 `null`이면 번호 없이 지도에만 찍히는 주변 장소다.

`Place.code`는 프로토타입이 쓰던 문자열 식별자(`'jd1'`)를 그대로 유지한 자연키다.
ETL을 다시 돌려도 같은 장소를 찾아내는 데 쓴다.

**이 엔티티들은 아직 API가 쓰지 않는다.** 장소는 data-server에서 그대로 넘겨주고 있다.
사용자가 일정을 저장하거나 장소를 찜하는 기능이 생기면 그때 채운다.

**아직 믿을 수 없는 값이 둘 있다.** 코드 주석에도 적어 뒀다.

- `Place.category` — 프로토타입의 `cat`을 옮긴 것이라 오분류가 있다.
  이름에 해수욕장·해변·해안이 들어간 91곳 중 41곳만 `SEA`이고 해운대해수욕장은 `NATURE`다.
  TourAPI 표준 분류로 다시 매기기 전까지 필터 기준으로 쓰지 않는다.
- `Place.dwellMinutes` — 전부 10분 단위이고 산출 근거가 기록돼 있지 않다.
  재산정 전까지 정렬·일정 계산의 근거로 삼지 않는다.

## API

전체 명세는 `/swagger-ui.html`.

### 장소·분석

data-server가 만든 결과를 넘겨준다. 로그인 없이 부를 수 있다.

| 메서드 | 경로 | 내용 |
|---|---|---|
| GET | `/api/v1/places` | 장소 1,171곳. `?region=경주`로 거른다 |
| GET | `/api/v1/tfi` | 지역×테마 강도(TFI) |
| GET | `/api/v1/staytime` | 지역 체류시간과 전국 대비 지수 |
| GET | `/api/v1/personas` | 사용자 유형과 군집 코드 |
| POST | `/api/v1/recommend` | 설문 응답 → 테마 추천 순위 |

**추천에는 `region`을 함께 보낸다.** 그래야 데이터랩 지역×테마 강도가 점수에 반영된다.
반영 여부는 응답의 `regionApplied`와 `sources`로 확인한다.

```
region 있음 → sources: ["국민여행조사", "외래관광객조사", "한국관광 데이터랩 (지역×테마 강도 TFI)"]
region 없음 → sources: ["국민여행조사", "외래관광객조사"]
```

`/api/v1/personas`의 테마 순위는 지역을 반영하지 않은 참고값이다. 실제 추천은 `/api/v1/recommend`로 받는다.

전체 장소 응답은 440KB다. 화면에서는 `?region=`으로 좁혀 쓴다.

data-server에 닿지 못하면 502 `DATA_SERVER_UNAVAILABLE`을 돌려준다.

### 인증

| 메서드 | 경로 | 인증 |
|---|---|---|
| POST | `/api/v1/auth/signup` | 불필요 |
| POST | `/api/v1/auth/login` | 불필요 |
| POST | `/api/v1/auth/reissue` | 불필요 |
| POST | `/api/v1/auth/logout` | access token |
| GET | `/api/v1/users/me` | access token |
| GET | `/health` | 불필요 |

자세한 규약은 `docs/security.md`.

## 들어 있지 않은 것

**코스 API.** 어느 장소가 어느 코스에 몇 번째로 들어가는지는 data-server가 아직 내려주지
않는다(`/v1/places`에 순번과 코스 이름이 없다). 나오면 중계를 붙인다.

그 밖에 장소 시드 데이터, 찜·일정 저장, 마이그레이션 도구, 소셜 로그인, 비밀번호 재설정,
이메일 발송, 파일 업로드, APM 에이전트.

스키마는 Hibernate `ddl-auto`가 만든다. 로컬은 `update`, 테스트는 `create-drop`이다.
운영에 올리기 전에 마이그레이션 도구를 붙이고 `validate`로 바꾼다 (`docs/entity.md`).

## 실행 방법

### 환경 설정

- JDK 21
- Docker (로컬 DB와 Testcontainers에 쓴다)

```bash
docker compose -f docker-compose.dev.yml up -d
cp .env.example .env
```

`.env.example`의 로컬 값은 위 컨테이너에 그대로 맞춰져 있다. 채워야 하는 건 둘이다.

| 이름 | 설명 |
|---|---|
| `JWT_SECRET` | HS256 서명 키. **32바이트 이상**이어야 기동한다. `openssl rand -base64 48` |
| `DATA_SERVER_URL` | data-server 주소. 없으면 기동하지 않는다 |

**`.env`를 읽어 주는 라이브러리는 없다.** 파일만 만들어 두면 값이 들어가지 않으니 직접 내보낸다.

```bash
set -a && . ./.env && set +a
```

data-server는 사설 IP(`172.31.15.78`)라 밖에서 닿지 않는다. 로컬에서 붙이려면 터널을 판다.

```bash
ssh -i <키> -f -N -L 8000:172.31.15.78:8000 ec2-user@<API 서버>
# 그리고 DATA_SERVER_URL=http://localhost:8000
```

### 실행

```bash
./gradlew installGitHooks   # 최초 1회
./gradlew bootRun
```

`installGitHooks`는 `core.hooksPath`를 `.githooks`로 바꿔 커밋할 때 `spotlessCheck`가 돌게 한다.

### 확인

- 헬스체크: `GET http://localhost:8080/health`
- API 문서: http://localhost:8080/swagger-ui.html

### 테스트

```bash
./gradlew test
```

### 커밋 전

```bash
./gradlew spotlessApply
```

pre-commit hook은 `spotlessCheck`로 검사만 하고 자동 수정은 하지 않는다.

> **주석은 `//`로 쓴다.** google-java-format 1.22.0이 Javadoc(`/** */`)을 재정렬할 때
> `NoSuchMethodError`로 죽어서 포맷 검사가 막힌다. 설명이 길면 `//` 여러 줄로 쓴다.

JDK가 여러 개 깔려 있으면 spotless가 21이 아닌 JDK를 잡아 같은 오류가 난다.

```bash
JAVA_HOME=/path/to/jdk-21 ./gradlew spotlessApply
```

## 배포

브랜치는 `develop`(개발·배포)과 `main`(릴리스) 2개를 쓴다 (`docs/git.md`).
기본 브랜치는 `develop`이다.

`cd.yml`은 `develop` 푸시마다 이미지를 GHCR에 올리고 EC2에서 `docker compose`로 띄운 뒤
`https://$DOMAIN/health`로 헬스체크한다.

| 종류 | 이름 | 용도 |
|---|---|---|
| Secret | `EC2_HOST` `EC2_USER` `EC2_SSH_KEY` | 배포 대상 접속 |
| Variable | `SERVICE_URL` | Environment 배포 링크 |

서버 준비(인스턴스·DB·보안 그룹·인증서)는 `docs/infra.md`에 있다.
배포 대상이 x86이면 `cd.yml`의 `platforms`를 `linux/amd64`로 바꾼다.

`release.yml`은 `main`으로 머지된 PR 중 제목이 `release: ... vX.Y.Z`인 것에 대해
GitHub Release를 만든다.
