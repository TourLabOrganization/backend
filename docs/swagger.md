# API 문서

springdoc-openapi가 스펙을 `/v3/api-docs`로, Swagger UI를 `/swagger-ui.html`로 낸다.
설정은 `global/config/SwaggerConfig` 한 곳에 두고,
에러 응답 어노테이션 두 개는 `global/annotation`에 둔다.

## 컨트롤러에 붙이는 것

| 대상 | 어노테이션 |
|---|---|
| 컨트롤러 클래스 | `@Tag(name = ..., description = ...)` |
| 핸들러 메서드 | `@Operation(summary = ..., description = ...)` |
| 요청·응답 record 필드 | `@Schema(description = ..., example = ...)` |
| 에러 응답 | `@ApiErrorCodeExample` / `@ApiErrorCodeExamples` |

`name`·`summary`·`description`은 한국어 한 문장으로 쓴다.

객체 필드에 `@Schema(nullable = true)`를 붙이면 OpenAPI 3.1에서 `type`이 `"null"`로 덮인다.
`SwaggerConfig`의 `OpenApiCustomizer`가 `["object", "null"]`로 되돌린다.

## 에러 응답

`ErrorCode`는 enum이라 어노테이션 요소 타입으로 그대로 쓸 수 있다.
`ApiErrorCodeExample`은 `ErrorCode value()`를, `ApiErrorCodeExamples`는 `ErrorCode[] value()`를 받는다.

- 에러 코드가 1개면 `@ApiErrorCodeExample(ErrorCode.RESOURCE_NOT_FOUND)`,
  2개 이상이면 `@ApiErrorCodeExamples({ErrorCode.BAD_REQUEST, ErrorCode.RESOURCE_NOT_FOUND})`를 쓴다.
  둘 다 요소 이름이 `value()`라 위치 인자로 쓴다
- **한 메서드에 둘을 같이 붙이지 않는다.** `SwaggerConfig`가 복수를 먼저 보고 단수를 무시한다
- code·message와 예시 JSON은 넘긴 상수에서 꺼내 만든다. 예시를 직접 쓰지 않는다
- 같은 status인 코드는 하나의 응답 아래 예시 여러 개로 묶인다
- 컨트롤러가 직접 던지지 않아도 **FE가 code로 분기해야 하는 것은 적는다.**
  서비스에서 던지는 코드도 포함이다
- 상수를 직접 넘기므로 없는 에러 코드는 컴파일러가 잡는다

## 인증

`bearerAuth`(HTTP bearer, JWT) security scheme이 전역으로 걸려 있다.
Swagger UI의 Authorize에 access token을 넣으면 그 토큰으로 인증된 요청이 나간다.
토큰은 `POST /api/v1/auth/login`으로 받는다 (`security.md`).
