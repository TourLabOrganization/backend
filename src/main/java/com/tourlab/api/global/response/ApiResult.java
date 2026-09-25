package com.tourlab.api.global.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

@Getter
@Schema(description = "공통 API 응답 포맷")
public class ApiResult<T> {

  @Schema(description = "커스텀 코드", example = "API_SUCCESS")
  private final String code;

  @Schema(description = "응답 메시지", example = "요청이 성공적으로 처리되었습니다.")
  private final String message;

  @Schema(description = "응답 데이터", nullable = true)
  private final T data;

  private ApiResult(String code, String message, T data) {
    this.code = code;
    this.message = message;
    this.data = data;
  }

  public static <T> ResponseEntity<ApiResult<T>> success(T data) {
    return success(SuccessCode.OK, data);
  }

  public static <T> ResponseEntity<ApiResult<T>> success(SuccessCode successCode, T data) {
    ApiResult<T> body = new ApiResult<>(successCode.getCode(), successCode.getMessage(), data);
    return ResponseEntity.status(successCode.getHttpStatus()).body(body);
  }
}
