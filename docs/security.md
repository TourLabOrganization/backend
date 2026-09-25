# 인증

이메일·비밀번호로 로그인하고 JWT를 발급하는 구조다.

| 하는 일 | 위치 |
|---|---|
| 토큰 발급·재발급·폐기 | `domain/auth` |
| 토큰 검증과 인가 | `global/security` + `global/config/SecurityConfig` |
| 사용자 저장 | `domain/user` |

## 엔드포인트

| 메서드 | 경로 | 인증 |
|---|---|---|
| POST | `/api/v1/auth/signup` | 불필요 |
| POST | `/api/v1/auth/login` | 불필요 |
| POST | `/api/v1/auth/reissue` | 불필요 (refresh token을 본문으로 받는다) |
| POST | `/api/v1/auth/logout` | access token |
| GET | `/api/v1/users/me` | access token |

공개 엔드포인트를 추가할 때는 `SecurityConfig`의 `PUBLIC_GET_PATHS` / `PUBLIC_POST_PATHS`에 넣는다.
메서드를 구분해서 등록한다. 경로만 열면 같은 경로의 다른 메서드까지 열린다.

## 토큰

- access token은 `Authorization: Bearer <token>` 헤더로 보낸다. 만료는 30분
- refresh token은 재발급 요청 본문으로만 보낸다. 만료는 14일
- 두 토큰은 `typ` 클레임(`access` / `refresh`)으로 구분한다.
  `SecurityConfig`의 디코더가 `typ=access`가 아닌 토큰을 거부하므로,
  refresh token을 `Authorization` 헤더에 실어도 통과하지 않는다
- 토큰에 담는 것은 사용자 ID(`sub`)와 `typ`뿐이다. 권한과 사용자 상태는 요청마다 DB에서 읽는다.
  토큰에 권한을 담으면 권한을 회수해도 토큰 만료까지 살아 있다

## refresh token 저장

`refresh_tokens` 테이블에 **SHA-256 해시로** 저장한다. 원문을 넣지 않는다.

- DB만 유출되면 저장된 값으로는 인증할 수 없다
- 비밀번호와 달리 salt를 쓰지 않는다. 토큰은 난수라 무차별 대입 대상이 아니다
- **재발급하면 쓴 토큰을 지우고 새로 발급한다(회전).** 같은 refresh token은 두 번 쓰이지 않는다
- 로그아웃은 그 사용자의 refresh token을 전부 지운다.
  access token은 만료까지 유효하다. 즉시 끊어야 하면 별도 차단 목록이 필요하다

만료된 행은 자동으로 지워지지 않는다.
`RefreshTokenRepository.deleteByExpiresAtBefore`가 있으니 주기적으로 부를 스케줄러가 필요하면
`@Scheduled`를 붙인 컴포넌트를 만들고 `@EnableScheduling`을 함께 켠다.

## 비밀번호

- `BCryptPasswordEncoder`로 해시해서 저장한다. `users.password`는 `VARCHAR(60)`,
  BCrypt 해시의 길이와 정확히 같다
- `PasswordEncoderFactories.createDelegatingPasswordEncoder()`를 쓰면 `{bcrypt}` 접두사가 붙어
  68자가 된다. 컬럼 길이를 함께 늘리지 않으면 저장 시점에 잘린다
- 로그인 실패는 이메일이 없든 비밀번호가 틀리든 `AUTH_LOGIN_FAILED` 하나로 응답한다.
  구분해서 응답하면 가입 여부를 확인하는 수단이 된다

## 인증 실패 응답

**`GlobalExceptionHandler`는 인증 실패를 잡지 못한다.**
인증은 서블릿 필터에서 끝나고, 예외가 `@RestControllerAdvice`까지 오지 않는다.

- 401은 `JwtAuthenticationEntryPoint`, 403은 `CustomAccessDeniedHandler`가 응답을 직접 만든다
- 두 핸들러는 `ErrorResult`를 직렬화해서 쓴다. 컨트롤러를 타는 에러와 본문 형식이 같다
- 인증 에러의 응답 형식을 바꿀 일이 생기면 이 두 곳과 `GlobalExceptionHandler`를 함께 고친다

## 컨트롤러에서 로그인 사용자 꺼내기

```java
public ResponseEntity<ApiResult<Xxx>> handler(@AuthenticationPrincipal UserPrincipal principal) {
  Long userId = principal.id();
}
```

`UserPrincipal`은 ID와 `Role`만 들고 있다. 사용자 정보가 더 필요하면 서비스에서 조회한다.

## 권한

- `Role`은 `USER` / `ADMIN`이고, `UserPrincipal.getAuthorities()`가 `ROLE_` 접두사를 붙인다
- `@EnableMethodSecurity`가 켜져 있어 `@PreAuthorize("hasRole('ADMIN')")`을 바로 쓸 수 있다
- 권한이 모자라면 `CustomAccessDeniedHandler`가 403 `AUTH_FORBIDDEN`으로 응답한다
