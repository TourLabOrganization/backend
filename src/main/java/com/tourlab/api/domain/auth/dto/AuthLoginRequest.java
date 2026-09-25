package com.tourlab.api.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "로그인 요청")
public record AuthLoginRequest(
    @Schema(description = "이메일", example = "user@example.com") @NotBlank String email,
    @Schema(description = "비밀번호", example = "password1234") @NotBlank String password) {}
