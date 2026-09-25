package com.tourlab.api.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "토큰 재발급 요청")
public record AuthReissueRequest(
    @Schema(description = "발급받은 refresh token") @NotBlank String refreshToken) {}
