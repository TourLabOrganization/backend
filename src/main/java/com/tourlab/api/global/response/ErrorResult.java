package com.tourlab.api.global.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.ResponseEntity;

@Schema(description = "공통 에러 응답 포맷")
public record ErrorResult(
    @Schema(description = "커스텀 코드", example = "RESOURCE_NOT_FOUND") String code,
    @Schema(description = "에러 메시지", example = "요청한 리소스를 찾을 수 없습니다.") String message) {

  public static ResponseEntity<ErrorResult> of(ErrorCode errorCode) {
    ErrorResult body = new ErrorResult(errorCode.getCode(), errorCode.getMessage());
    return ResponseEntity.status(errorCode.getHttpStatus()).body(body);
  }
}
