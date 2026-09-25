# 네이밍

## 클래스

| 대상 | 규칙 | 예 |
|---|---|---|
| 컨트롤러 | `<도메인>Controller` | `ArticleController` |
| 서비스 | `<도메인>Service` | `ArticleService` |
| 리포지토리 | `<엔티티>Repository` | `ArticleCommentRepository` |
| 엔티티 | 테이블명의 PascalCase, 접미사 없음 | `ArticleComment` |
| 요청 DTO | `<도메인><행위>Request` | `ArticleCreateRequest` |
| 응답 DTO | `<도메인><행위>Response` | `ArticleCreateResponse` |
| 설정 | `<대상>Config` | `SwaggerConfig` |

- **`ArticleDto` 같은 제네릭한 이름을 쓰지 않는다.**
  엔드포인트에 묶지 않으면 필드가 20개까지 늘어나고, 한 화면 때문에 다른 화면의 응답이 바뀐다
- 서비스 인터페이스는 만들지 않는다. 구현체가 1개면 클래스 하나로 충분하다.
  `*Impl` 접미사도 쓰지 않는다

## 메서드

| 상황 | 규칙 |
|---|---|
| 조회 — 결과가 없을 수 있음 | `findXxx` → `Optional<T>` 반환 |
| 조회 — 없으면 예외 | `getXxx` → 없으면 `ApiException` |
| boolean | `isXxx` / `hasXxx` / `existsXxx` |
| 상태 변경 | 도메인 언어로 쓴다. `publish()`, `close()`. `setXxx`는 쓰지 않는다 |

## API 경로

- 경로는 `/api/v1/` 아래에 둔다.
  헬스체크처럼 인프라가 호출하는 엔드포인트만 예외로 밖에 둔다. 현재는 `/health` 하나다
- 컬렉션 세그먼트는 **복수 명사 + 소문자 케밥**, 경로 변수는 camelCase로 쓴다.
  예: `/api/v1/articles/{articleId}/comments`
- 컬렉션이 아닌 하위 속성이나 행위 세그먼트는 단수 케밥을 허용한다.
  예: `/articles/{articleId}/view-count`

## 패키지

- 패키지는 **전부 소문자**로 쓰고 언더스코어를 넣지 않는다 (Google Java Style §5.2.1)
- 도메인 코드는 `domain.<도메인>` 아래
  `controller` / `service` / `repository` / `entity` / `dto`로 나눈다
- 이 5개는 최소 집합이고, 닫힌 목록은 아니다.
  필요하면 폴더를 추가한다(예: `exception`, `client`)
- 전 도메인 공통만 `global` 아래에 둔다(`response` / `exception` / `config` / `health` 등,
  닫힌 목록 아님)
- **`global` 승격 규칙**: 도메인 2개 이상이 실제로 import하는 것만 `global`에 둔다.
  1개면 그 도메인 안에 두고, 두 번째 도메인이 쓰는 PR에서 올린다

## DB

- 테이블명은 `snake_case` + **복수**로 쓴다. 엔티티 클래스명은 단수, 테이블명은 복수다.
  예: `User` + `@Table(name = "users")`, `ArticleComment` + `@Table(name = "article_comments")`
- 컬럼명은 PK는 `id` / FK는 `<참조테이블>_id` / 시각은 `*_at` / boolean은 `is_*`로 쓴다

DDL은 따로 두지 않고 Hibernate가 엔티티에서 만든다 (`docs/entity.md`).
기존 테이블·컬럼 이름을 확인할 때는 엔티티의 `@Table` / `@Column`을 본다.

## 테스트

- 테스트 클래스는 `<대상>Test`로 만든다
- **검증 내용은 `@DisplayName`에 한국어 문장 하나로 쓴다.**
  "삭제된 게시글의 댓글은 조회되지 않는다"처럼 기대 결과가 문장으로 읽혀야 한다
- 메서드명은 그 문장을 영문으로 짧게 옮긴 것으로 한다
- `기능_상황_기대결과` 같은 별도 규칙은 두지 않는다.
  `@DisplayName`과 같은 정보를 두 번 쓰게 된다
