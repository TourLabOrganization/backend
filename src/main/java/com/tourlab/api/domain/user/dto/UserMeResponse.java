package com.tourlab.api.domain.user.dto;

import com.tourlab.api.domain.user.entity.Role;
import com.tourlab.api.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "내 정보 응답")
public record UserMeResponse(
    @Schema(description = "사용자 ID", example = "1") Long id,
    @Schema(description = "이메일", example = "user@example.com") String email,
    @Schema(description = "닉네임", example = "홍길동") String nickname,
    @Schema(description = "권한", example = "USER") Role role) {

  public static UserMeResponse from(User user) {
    return new UserMeResponse(user.getId(), user.getEmail(), user.getNickname(), user.getRole());
  }
}
