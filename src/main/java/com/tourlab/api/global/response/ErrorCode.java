package com.tourlab.api.global.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {

  // 400 Bad Request
  BAD_REQUEST(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "요청 파라미터가 올바르지 않습니다."),
  INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "INVALID_INPUT_VALUE", "입력값 검증에 실패했습니다."),
  INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "INVALID_TYPE_VALUE", "요청 값의 타입이 올바르지 않습니다."),
  MISSING_REQUEST_PARAMETER(
      HttpStatus.BAD_REQUEST, "MISSING_REQUEST_PARAMETER", "필수 요청 파라미터가 누락되었습니다."),
  MISSING_REQUEST_HEADER(HttpStatus.BAD_REQUEST, "MISSING_REQUEST_HEADER", "필수 요청 헤더가 누락되었습니다."),
  MALFORMED_REQUEST_BODY(HttpStatus.BAD_REQUEST, "MALFORMED_REQUEST_BODY", "요청 본문을 읽을 수 없습니다."),

  // 401 Unauthorized
  AUTH_LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "AUTH_LOGIN_FAILED", "이메일 또는 비밀번호가 올바르지 않습니다."),
  AUTH_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "AUTH_TOKEN_INVALID", "유효하지 않은 인증 토큰입니다."),
  AUTH_REFRESH_TOKEN_INVALID(
      HttpStatus.UNAUTHORIZED, "AUTH_REFRESH_TOKEN_INVALID", "유효하지 않은 refresh token입니다."),
  AUTH_REFRESH_TOKEN_EXPIRED(
      HttpStatus.UNAUTHORIZED, "AUTH_REFRESH_TOKEN_EXPIRED", "만료된 refresh token입니다. 다시 로그인해 주세요."),

  // 403 Forbidden
  AUTH_FORBIDDEN(HttpStatus.FORBIDDEN, "AUTH_FORBIDDEN", "접근 권한이 없습니다."),

  // 404 Not Found
  RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", "요청한 리소스를 찾을 수 없습니다."),
  USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "존재하지 않는 사용자입니다."),

  // 405 Method Not Allowed
  METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "METHOD_NOT_ALLOWED", "허용되지 않은 HTTP 메서드입니다."),

  // 409 Conflict
  USER_EMAIL_DUPLICATED(HttpStatus.CONFLICT, "USER_EMAIL_DUPLICATED", "이미 가입된 이메일입니다."),

  // 415 Unsupported Media Type
  UNSUPPORTED_MEDIA_TYPE(
      HttpStatus.UNSUPPORTED_MEDIA_TYPE, "UNSUPPORTED_MEDIA_TYPE", "지원하지 않는 Content-Type입니다."),

  // 502 Bad Gateway
  DATA_SERVER_UNAVAILABLE(HttpStatus.BAD_GATEWAY, "DATA_SERVER_UNAVAILABLE", "데이터 서버에 연결할 수 없습니다."),

  // 500 Internal Server Error
  INTERNAL_SERVER_ERROR(
      HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "알 수 없는 서버 오류가 발생했습니다.");

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;
}
