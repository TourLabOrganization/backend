package com.tourlab.api.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "회원가입 요청")
public record AuthSignupRequest(
    @Schema(description = "이메일", example = "user@example.com") @NotBlank @Email @Size(max = 255)
        String email,
    @Schema(description = "비밀번호", example = "password1234") @NotBlank @Size(min = 8, max = 64)
        String password,
    @Schema(description = "닉네임", example = "홍길동") @NotBlank @Size(max = 20) String nickname) {}
