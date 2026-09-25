package com.tourlab.api.domain.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tourlab.api.TestcontainersConfiguration;
import com.tourlab.api.domain.auth.dto.AuthLoginRequest;
import com.tourlab.api.domain.auth.dto.AuthReissueRequest;
import com.tourlab.api.domain.auth.dto.AuthSignupRequest;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@AutoConfigureMockMvc
class AuthIntegrationTest {

  private static final AtomicInteger SEQUENCE = new AtomicInteger();
  private static final String PASSWORD = "password1234";

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Test
  @DisplayName("회원가입한 이메일과 비밀번호로 로그인하면 access token과 refresh token을 받는다")
  void issuesTokensAfterSignup() throws Exception {
    String email = signup();

    JsonNode tokens = login(email);

    assertThat(tokens.get("accessToken").asString()).isNotBlank();
    assertThat(tokens.get("refreshToken").asString()).isNotBlank();
  }

  @Test
  @DisplayName("이미 가입된 이메일로 다시 회원가입하면 409로 거절된다")
  void rejectsDuplicateEmail() throws Exception {
    String email = signup();

    mockMvc
        .perform(
            post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(new AuthSignupRequest(email, PASSWORD, "중복"))))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.code").value("USER_EMAIL_DUPLICATED"));
  }

  @Test
  @DisplayName("비밀번호가 틀리면 401로 거절되고 이메일 존재 여부는 드러나지 않는다")
  void rejectsWrongPassword() throws Exception {
    String email = signup();

    mockMvc
        .perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(new AuthLoginRequest(email, "wrongpassword"))))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.code").value("AUTH_LOGIN_FAILED"));

    mockMvc
        .perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        new AuthLoginRequest("nobody@example.com", PASSWORD))))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.code").value("AUTH_LOGIN_FAILED"));
  }

  @Test
  @DisplayName("access token 없이 내 정보를 조회하면 401로 거절된다")
  void rejectsMissingToken() throws Exception {
    mockMvc
        .perform(get("/api/v1/users/me"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.code").value("AUTH_TOKEN_INVALID"));
  }

  @Test
  @DisplayName("access token으로 내 정보를 조회하면 가입할 때 쓴 이메일이 나온다")
  void returnsMyInfo() throws Exception {
    String email = signup();
    JsonNode tokens = login(email);

    mockMvc
        .perform(bearer(get("/api/v1/users/me"), tokens.get("accessToken").asString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.email").value(email))
        .andExpect(jsonPath("$.data.role").value("USER"));
  }

  @Test
  @DisplayName("refresh token을 Authorization 헤더에 실어 보내면 401로 거절된다")
  void rejectsRefreshTokenAsAccessToken() throws Exception {
    String email = signup();
    JsonNode tokens = login(email);

    mockMvc
        .perform(bearer(get("/api/v1/users/me"), tokens.get("refreshToken").asString()))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.code").value("AUTH_TOKEN_INVALID"));
  }

  @Test
  @DisplayName("재발급에 쓴 refresh token은 즉시 폐기되어 두 번 쓸 수 없다")
  void rotatesRefreshToken() throws Exception {
    String email = signup();
    String refreshToken = login(email).get("refreshToken").asString();

    mockMvc
        .perform(
            post("/api/v1/auth/reissue")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new AuthReissueRequest(refreshToken))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.accessToken").isNotEmpty());

    mockMvc
        .perform(
            post("/api/v1/auth/reissue")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new AuthReissueRequest(refreshToken))))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.code").value("AUTH_REFRESH_TOKEN_INVALID"));
  }

  @Test
  @DisplayName("로그아웃하면 발급받은 refresh token으로 재발급받을 수 없다")
  void invalidatesRefreshTokenOnLogout() throws Exception {
    String email = signup();
    JsonNode tokens = login(email);

    mockMvc
        .perform(bearer(post("/api/v1/auth/logout"), tokens.get("accessToken").asString()))
        .andExpect(status().isOk());

    mockMvc
        .perform(
            post("/api/v1/auth/reissue")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        new AuthReissueRequest(tokens.get("refreshToken").asString()))))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.code").value("AUTH_REFRESH_TOKEN_INVALID"));
  }

  @Test
  @DisplayName("헬스체크는 인증 없이 열려 있다")
  void allowsHealthCheckWithoutToken() throws Exception {
    mockMvc.perform(get("/health")).andExpect(status().isOk());
  }

  private String signup() throws Exception {
    String email = "user%d@example.com".formatted(SEQUENCE.incrementAndGet());
    mockMvc
        .perform(
            post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(new AuthSignupRequest(email, PASSWORD, "테스터"))))
        .andExpect(status().isCreated());
    return email;
  }

  private JsonNode login(String email) throws Exception {
    String body =
        mockMvc
            .perform(
                post("/api/v1/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        objectMapper.writeValueAsString(new AuthLoginRequest(email, PASSWORD))))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    return objectMapper.readTree(body).get("data");
  }

  private static MockHttpServletRequestBuilder bearer(
      MockHttpServletRequestBuilder builder, String token) {
    return builder.header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
  }
}
