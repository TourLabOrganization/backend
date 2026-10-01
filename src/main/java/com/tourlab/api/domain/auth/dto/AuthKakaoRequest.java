package com.tourlab.api.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "카카오 로그인 요청")
public record AuthKakaoRequest(
    @Schema(description = "카카오 SDK에서 발급받은 access token") @NotBlank String accessToken) {

  @Override
  public String toString() {
    return "AuthKakaoRequest[accessToken=***]";
  }
}
