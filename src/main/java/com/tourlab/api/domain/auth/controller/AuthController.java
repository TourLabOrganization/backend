package com.tourlab.api.domain.auth.controller;

import com.tourlab.api.domain.auth.dto.AuthLoginRequest;
import com.tourlab.api.domain.auth.dto.AuthReissueRequest;
import com.tourlab.api.domain.auth.dto.AuthSignupRequest;
import com.tourlab.api.domain.auth.dto.AuthSignupResponse;
import com.tourlab.api.domain.auth.dto.AuthTokenResponse;
import com.tourlab.api.domain.auth.service.AuthService;
import com.tourlab.api.global.annotation.ApiErrorCodeExample;
import com.tourlab.api.global.annotation.ApiErrorCodeExamples;
import com.tourlab.api.global.response.ApiResult;
import com.tourlab.api.global.response.ErrorCode;
import com.tourlab.api.global.response.SuccessCode;
import com.tourlab.api.global.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "회원가입·로그인 API")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @Operation(summary = "회원가입", description = "이메일과 비밀번호로 계정을 만든다.")
  @ApiErrorCodeExamples({ErrorCode.INVALID_INPUT_VALUE, ErrorCode.USER_EMAIL_DUPLICATED})
  @PostMapping("/signup")
  public ResponseEntity<ApiResult<AuthSignupResponse>> signup(
      @Valid @RequestBody AuthSignupRequest request) {
    return ApiResult.success(SuccessCode.CREATED, authService.signup(request));
  }

  @Operation(summary = "로그인", description = "이메일과 비밀번호를 확인하고 access·refresh token을 발급한다.")
  @ApiErrorCodeExample(ErrorCode.AUTH_LOGIN_FAILED)
  @PostMapping("/login")
  public ResponseEntity<ApiResult<AuthTokenResponse>> login(
      @Valid @RequestBody AuthLoginRequest request) {
    return ApiResult.success(authService.login(request));
  }

  @Operation(
      summary = "토큰 재발급",
      description = "refresh token으로 access·refresh token을 새로 발급한다. 쓴 refresh token은 즉시 폐기된다.")
  @ApiErrorCodeExamples({
    ErrorCode.AUTH_REFRESH_TOKEN_INVALID,
    ErrorCode.AUTH_REFRESH_TOKEN_EXPIRED
  })
  @PostMapping("/reissue")
  public ResponseEntity<ApiResult<AuthTokenResponse>> reissue(
      @Valid @RequestBody AuthReissueRequest request) {
    return ApiResult.success(authService.reissue(request));
  }

  @Operation(summary = "로그아웃", description = "이 사용자의 refresh token을 모두 폐기한다.")
  @ApiErrorCodeExample(ErrorCode.AUTH_TOKEN_INVALID)
  @PostMapping("/logout")
  public ResponseEntity<ApiResult<Void>> logout(@AuthenticationPrincipal UserPrincipal principal) {
    authService.logout(principal.id());
    return ApiResult.success(null);
  }
}
