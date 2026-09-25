package com.tourlab.api.domain.auth.dto;

import com.tourlab.api.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원가입 응답")
public record AuthSignupResponse(
    @Schema(description = "생성된 사용자 ID", example = "1") Long userId,
    @Schema(description = "이메일", example = "user@example.com") String email) {

  public static AuthSignupResponse from(User user) {
    return new AuthSignupResponse(user.getId(), user.getEmail());
  }
}
