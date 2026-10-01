package com.tourlab.api.domain.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.tourlab.api.domain.auth.client.KakaoClient;
import com.tourlab.api.domain.auth.client.KakaoProperties;
import com.tourlab.api.global.exception.ApiException;
import com.tourlab.api.global.response.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class KakaoClientTest {

  private static final String URL = "https://kapi.kakao.com/v1/user/access_token_info";
  private static final String TOKEN = "kakao-access-token";

  @Test
  @DisplayName("유효한 카카오 토큰의 앱 ID가 일치하면 사용자 ID를 반환한다")
  void returnsUserIdWhenTokenBelongsToApp() {
    RestClient.Builder builder = RestClient.builder().baseUrl("https://kapi.kakao.com");
    MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
    server
        .expect(once(), requestTo(URL))
        .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer " + TOKEN))
        .andRespond(
            withSuccess(
                "{\"id\":123,\"app_id\":456,\"expires_in\":100}", MediaType.APPLICATION_JSON));

    assertThat(new KakaoClient(builder.build(), new KakaoProperties(456L)).getVerifiedUserId(TOKEN))
        .isEqualTo(123L);
    server.verify();
  }

  @Test
  @DisplayName("다른 카카오 앱에서 발급된 토큰은 거절한다")
  void rejectsTokenForAnotherApp() {
    RestClient.Builder builder = RestClient.builder().baseUrl("https://kapi.kakao.com");
    MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
    server
        .expect(requestTo(URL))
        .andRespond(withSuccess("{\"id\":123,\"app_id\":789}", MediaType.APPLICATION_JSON));

    assertThatThrownBy(
            () ->
                new KakaoClient(builder.build(), new KakaoProperties(456L))
                    .getVerifiedUserId(TOKEN))
        .isInstanceOf(ApiException.class)
        .extracting("errorCode")
        .isEqualTo(ErrorCode.AUTH_KAKAO_TOKEN_INVALID);
    server.verify();
  }

  @Test
  @DisplayName("만료되거나 잘못된 카카오 토큰은 인증 실패로 처리한다")
  void rejectsInvalidToken() {
    RestClient.Builder builder = RestClient.builder().baseUrl("https://kapi.kakao.com");
    MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
    server.expect(requestTo(URL)).andRespond(withStatus(HttpStatus.UNAUTHORIZED));

    assertThatThrownBy(
            () ->
                new KakaoClient(builder.build(), new KakaoProperties(456L))
                    .getVerifiedUserId(TOKEN))
        .isInstanceOf(ApiException.class)
        .extracting("errorCode")
        .isEqualTo(ErrorCode.AUTH_KAKAO_TOKEN_INVALID);
    server.verify();
  }

  @Test
  @DisplayName("카카오 인증 서버 오류는 잘못된 토큰과 구분해 502로 처리한다")
  void reportsKakaoServerFailure() {
    RestClient.Builder builder = RestClient.builder().baseUrl("https://kapi.kakao.com");
    MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
    server.expect(requestTo(URL)).andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

    assertThatThrownBy(
            () ->
                new KakaoClient(builder.build(), new KakaoProperties(456L))
                    .getVerifiedUserId(TOKEN))
        .isInstanceOf(ApiException.class)
        .extracting("errorCode")
        .isEqualTo(ErrorCode.KAKAO_UNAVAILABLE);
    server.verify();
  }
}
