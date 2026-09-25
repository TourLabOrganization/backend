package com.tourlab.api.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "토큰 발급 응답")
public record AuthTokenResponse(
    @Schema(description = "access token. Authorization 헤더에 Bearer로 실어 보낸다") String accessToken,
    @Schema(description = "refresh token. 재발급에만 쓴다") String refreshToken) {}
