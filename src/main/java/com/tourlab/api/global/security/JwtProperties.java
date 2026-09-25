package com.tourlab.api.global.security;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
    @NotBlank @Size(min = 32) String secret,
    @NotNull Duration accessTokenExpiry,
    @NotNull Duration refreshTokenExpiry) {}
