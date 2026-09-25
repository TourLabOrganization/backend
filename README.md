# Spring Boot Backend Boilerplate

Spring Boot 4 백엔드 프로젝트의 시작점.
공통 응답·예외 처리·API 문서 설정, 이메일 로그인, CI/CD, 컨벤션 문서를 담고 있다.

## 기술 스택

| 구분 | 사용 기술 |
|---|---|
| 언어 | Java 21 |
| 프레임워크 | Spring Boot 4.0.7 (Spring Framework 7) |
| 빌드 | Gradle 9.5.1 (wrapper) |
| 데이터베이스 | MySQL |
| API 문서 | springdoc-openapi 3.0.3 (Swagger UI) |
| 인증 | Spring Security 7 + JWT (oauth2 resource server) |
| 코드 포맷 | Spotless + google-java-format 1.22.0 |
| 배포 | GitHub Actions → GHCR → EC2 (arm64) + nginx + certbot |

## 들어 있는 것

| 영역 | 내용 |
|---|---|
| 공통 응답 | `ApiResult` / `ErrorResult` / `SuccessCode` / `ErrorCode` |
| 예외 처리 | `ApiException` + `GlobalExceptionHandler` (12개 핸들러) |
| API 문서 | `SwaggerConfig` + `@ApiErrorCodeExample(s)` — 에러 코드 상수에서 응답 예시 생성 |
| 인증 | 이메일·비밀번호 로그인, JWT access/refresh 발급, refresh token 회전, 로그아웃 |
| 인가 | `Role`(USER/ADMIN) + `@EnableMethodSecurity`, 401/403 JSON 응답 핸들러 |
| JPA | `BaseTimeEntity` + `JpaAuditingConfig`, `users`·`refresh_tokens` 엔티티 |
| 헬스체크 | `GET /health` |
| 툴링 | Spotless, `lombok.config`(위험한 lombok 애노테이션 컴파일 차단), pre-commit hook |
| 테스트 | Testcontainers(MySQL 8.4) 기반 컨텍스트 로드 + 인증 흐름 통합 테스트 |
| CI/CD | CI(포맷+빌드+테스트), CD(GHCR 이미지 → EC2 배포 → 헬스체크), Release(PR 제목 기반 태깅) |
| 컨벤션 | `CLAUDE.md` + `docs/` 8개 문서 (`AGENTS.md`는 `CLAUDE.md`를 가리키는 포인터) |

## API

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

마이그레이션 도구, 소셜 로그인, 비밀번호 재설정, 이메일 발송, 외부 API 클라이언트,
파일 업로드, APM 에이전트. 필요하면 각 프로젝트에서 추가한다.

스키마는 Hibernate `ddl-auto`가 만든다. 로컬은 `update`, 테스트는 `create-drop`이다.
운영에 올리기 전에 마이그레이션 도구를 붙이고 `validate`로 바꾼다 (`docs/entity.md`).

## 새 프로젝트로 시작하기

루트 패키지는 `com.tourlab.api`, 프로젝트명은 `boilerplate`다. 둘 다 바꾼다.

```bash
OLD_PKG_PATH=com/tourlab/api
NEW_PKG_PATH=com/myteam/myapp
OLD_PKG=com.tourlab.api
NEW_PKG=com.myteam.myapp

for ROOT in src/main/java src/test/java; do
  mkdir -p "$ROOT/$NEW_PKG_PATH"
  git mv "$ROOT/$OLD_PKG_PATH"/* "$ROOT/$NEW_PKG_PATH"/
done
find src -name '*.java' -exec perl -pi -e "s|\Q$OLD_PKG\E|$NEW_PKG|g" {} +

git mv src/main/java/$NEW_PKG_PATH/BoilerplateApplication.java \
       src/main/java/$NEW_PKG_PATH/MyAppApplication.java
git mv src/test/java/$NEW_PKG_PATH/BoilerplateApplicationTests.java \
       src/test/java/$NEW_PKG_PATH/MyAppApplicationTests.java
find src -name '*.java' -exec perl -pi -e 's|BoilerplateApplication|MyAppApplication|g' {} +
```

`sed`가 아니라 `perl`을 쓰는 이유는 in-place 옵션의 인자 규칙이 BSD sed(macOS)와
GNU sed(Linux)에서 달라서다. `perl -pi -e`는 양쪽에서 같게 동작한다.

그다음 손으로 고칠 곳:

- `settings.gradle` — `rootProject.name`
- `build.gradle` — `group`
- `src/main/resources/application.yaml` — `spring.application.name`
- `src/main/java/.../global/config/SwaggerConfig.java` — `Info`의 title, description
- `CLAUDE.md` — 첫 줄 제목
- `README.md` — 이 파일 전체를 프로젝트 설명으로 바꾼다

`docs/`는 손댈 것이 없다. 프로젝트 고유 값을 박아두지 않았다.

빈 디렉터리가 남으면 지운다: `find src -type d -empty -delete`

## 실행 방법

### 환경 설정

- JDK 21
- MySQL
- Docker (테스트에 Testcontainers를 쓴다)

```bash
cp .env.example .env
```

`.env`에 채울 값:

| 이름 | 설명 |
|---|---|
| `DB_URL` `DB_USERNAME` `DB_PASSWORD` | MySQL 접속 정보 |
| `JWT_SECRET` | HS256 서명 키. **32자 이상**이어야 기동한다 |
| `CORS_ALLOWED_ORIGINS` | 허용할 오리진. 쉼표로 여러 개 |

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

## 배포 설정

브랜치는 `develop`(개발·배포)과 `main`(릴리스) 2개를 쓴다 (`docs/git.md`).
기본 브랜치는 `develop`이다.

`cd.yml`은 `develop` 푸시마다 이미지를 GHCR에 올리고 EC2에서 `docker compose`로 띄운 뒤
`https://$DOMAIN/health`로 헬스체크한다.

GitHub 저장소에 필요한 값:

| 종류 | 이름 | 용도 |
|---|---|---|
| Secret | `EC2_HOST` `EC2_USER` `EC2_SSH_KEY` | 배포 대상 접속 |
| Variable | `SERVICE_URL` | Environment 배포 링크 |

EC2에 미리 준비할 것:

- `docker` + `docker-compose-plugin`
- `~/app/app.env` — 애플리케이션 환경변수 + `DOMAIN=배포도메인`
- certbot 인증서 최초 발급 (이후 갱신은 `certbot` 컨테이너가 12시간마다 돈다)

배포 대상이 x86이면 `cd.yml`의 `platforms`를 `linux/amd64`로 바꾼다.

`release.yml`은 `main`으로 머지된 PR 중 제목이 `release: ... vX.Y.Z`인 것에 대해
GitHub Release를 만든다.
