package com.tourlab.api.domain.auth.service;

import com.tourlab.api.domain.auth.dto.AuthLoginRequest;
import com.tourlab.api.domain.auth.dto.AuthReissueRequest;
import com.tourlab.api.domain.auth.dto.AuthSignupRequest;
import com.tourlab.api.domain.auth.dto.AuthSignupResponse;
import com.tourlab.api.domain.auth.dto.AuthTokenResponse;
import com.tourlab.api.domain.auth.entity.RefreshToken;
import com.tourlab.api.domain.auth.repository.RefreshTokenRepository;
import com.tourlab.api.domain.user.entity.User;
import com.tourlab.api.domain.user.repository.UserRepository;
import com.tourlab.api.global.exception.ApiException;
import com.tourlab.api.global.response.ErrorCode;
import com.tourlab.api.global.security.service.JwtProvider;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

  private final UserRepository userRepository;
  private final RefreshTokenRepository refreshTokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtProvider jwtProvider;

  @Transactional
  public AuthSignupResponse signup(AuthSignupRequest request) {
    if (userRepository.existsByEmail(request.email())) {
      throw ApiException.of(ErrorCode.USER_EMAIL_DUPLICATED);
    }

    User user =
        new User(request.email(), passwordEncoder.encode(request.password()), request.nickname());
    userRepository.save(user);
    log.info("[회원가입] userId={}", user.getId());
    return AuthSignupResponse.from(user);
  }

  @Transactional
  public AuthTokenResponse login(AuthLoginRequest request) {
    User user =
        userRepository
            .findByEmail(request.email())
            .orElseThrow(() -> ApiException.of(ErrorCode.AUTH_LOGIN_FAILED));

    if (!passwordEncoder.matches(request.password(), user.getPassword())) {
      throw ApiException.of(ErrorCode.AUTH_LOGIN_FAILED);
    }

    log.info("[로그인] userId={}", user.getId());
    return issueTokens(user);
  }

  @Transactional
  public AuthTokenResponse reissue(AuthReissueRequest request) {
    Long userId = jwtProvider.parseRefreshTokenSubject(request.refreshToken());

    RefreshToken stored =
        refreshTokenRepository
            .findByTokenHash(hash(request.refreshToken()))
            .orElseThrow(() -> ApiException.of(ErrorCode.AUTH_REFRESH_TOKEN_INVALID));

    if (!stored.getUser().getId().equals(userId)) {
      throw ApiException.of(ErrorCode.AUTH_REFRESH_TOKEN_INVALID);
    }

    refreshTokenRepository.delete(stored);

    if (stored.isExpired(LocalDateTime.now())) {
      throw ApiException.of(ErrorCode.AUTH_REFRESH_TOKEN_EXPIRED);
    }

    return issueTokens(stored.getUser());
  }

  @Transactional
  public void logout(Long userId) {
    refreshTokenRepository.deleteByUserId(userId);
    log.info("[로그아웃] userId={}", userId);
  }

  private AuthTokenResponse issueTokens(User user) {
    String accessToken = jwtProvider.createAccessToken(user.getId());
    String refreshToken = jwtProvider.createRefreshToken(user.getId());

    refreshTokenRepository.save(
        new RefreshToken(
            user, hash(refreshToken), LocalDateTime.now().plus(jwtProvider.refreshTokenExpiry())));

    return new AuthTokenResponse(accessToken, refreshToken);
  }

  private String hash(String token) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      return HexFormat.of().formatHex(digest.digest(token.getBytes(StandardCharsets.UTF_8)));
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("SHA-256을 사용할 수 없습니다.", e);
    }
  }
}
