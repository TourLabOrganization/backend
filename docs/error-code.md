# 에러 코드

## ErrorCode

`global/response/ErrorCode` 하나의 enum에 공통 에러와 도메인 에러를 전부 모아서 관리한다.
인터페이스나 도메인별 `<도메인>ErrorCode`로 쪼개지 않는다.

도메인 에러가 필요하면 이 enum에 상수를 바로 추가한다.
HTTP 상태 오름차순으로 그룹을 나누고 `// 4xx ...` 주석으로 구분한다 — 기존 그룹에 없는
상태면 새 그룹을 추가한다.

## 상수 규칙

- 도메인에서 추가하는 code 문자열은 `<도메인>_` 접두사로 시작한다. 예: `AUTH_TOKEN_INVALID`,
  `ARTICLE_NOT_FOUND`
- `message`는 사용자에게 그대로 노출해도 되는 한국어 문장으로 쓴다.
  FE는 code로 분기하고, message는 기본 노출 문구다

## 에러 응답 본문

에러 응답은 `global/response/ErrorResult`로 나간다. 필드는 `code`와 `message` 둘뿐이다.

```json
{ "code": "RESOURCE_NOT_FOUND", "message": "요청한 리소스를 찾을 수 없습니다." }
```

- **HTTP 상태 코드를 본문에 넣지 않는다.** 상태줄에 이미 있고, 두 값이 어긋날 자리만 만든다
- **에러 본문에 데이터를 싣지 않는다.** 식별자를 실으면 리소스 존재 여부가 새어 나간다.
  필드별 검증 메시지가 필요해지면 아무 값이나 담기는 `data`가 아니라 전용 필드를 추가한다
- 성공은 `ApiResult`, 에러는 `ErrorResult`로 타입이 다르다.
  `ApiResult`에 `fail()`이 없으므로 컨트롤러에서 에러 본문을 반환하면 컴파일되지 않는다

## SuccessCode

- 기본은 `OK`를 쓴다. HTTP 상태나 메시지가 달라야 하는 응답에만 상수를 추가한다.
  201로 응답하는 생성 API는 `CREATED`를 쓴다
