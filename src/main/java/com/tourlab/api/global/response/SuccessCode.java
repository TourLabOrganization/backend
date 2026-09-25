package com.tourlab.api.global.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum SuccessCode {
  OK(HttpStatus.OK, "API_SUCCESS", "요청이 성공적으로 처리되었습니다."),
  CREATED(HttpStatus.CREATED, "API_CREATED", "리소스가 성공적으로 생성되었습니다.");

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;
}
