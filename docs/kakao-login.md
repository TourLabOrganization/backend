# 카카오 로그인 적용

프론트는 Kakao SDK에서 받은 **카카오 access token**을 다음처럼 보낸다.

```http
POST /api/v1/auth/kakao
Content-Type: application/json

{"accessToken":"카카오 access token"}
```

응답은 기존 `AuthTokenResponse`와 같다. 이후 API에는 응답의 **자체 access token**을
`Authorization: Bearer <token>`으로 사용한다. 카카오 토큰은 이 API에만 전달한다.

## 설정

카카오 개발자 콘솔의 **앱 ID(숫자)**를 `KAKAO_APP_ID`에 설정한다.
JavaScript 키나 REST API 키가 아니다. 카카오 로그인 사용 설정과 프론트 SDK의 앱 키 설정도 필요하다.
서버는 토큰 정보를 카카오 API에서 조회하고 `app_id`가 이 값과 일치하는지 확인한다.

