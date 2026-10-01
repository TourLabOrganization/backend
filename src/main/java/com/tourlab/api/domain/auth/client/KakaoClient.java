package com.tourlab.api.domain.auth.client;

import com.tourlab.api.global.exception.ApiException;
import com.tourlab.api.global.response.ErrorCode;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import tools.jackson.databind.JsonNode;

@Component
public class KakaoClient {

  private final RestClient restClient;
  private final KakaoProperties properties;

  public KakaoClient(
      @Qualifier("kakaoRestClient") RestClient restClient, KakaoProperties properties) {
    this.restClient = restClient;
    this.properties = properties;
  }

  public Long getVerifiedUserId(String accessToken) {
    JsonNode response;
    try {
      response =
          restClient
              .get()
              .uri("/v1/user/access_token_info")
              .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
              .retrieve()
              .body(JsonNode.class);
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode().value() == 400 || e.getStatusCode().value() == 401) {
        throw ApiException.of(ErrorCode.AUTH_KAKAO_TOKEN_INVALID);
      }
      throw ApiException.of(ErrorCode.KAKAO_UNAVAILABLE);
    } catch (RestClientException e) {
      throw ApiException.of(ErrorCode.KAKAO_UNAVAILABLE);
    }

    if (response == null
        || !response.path("id").canConvertToLong()
        || !response.path("app_id").canConvertToLong()) {
      throw ApiException.of(ErrorCode.KAKAO_UNAVAILABLE);
    }
    if (response.path("id").longValue() <= 0) {
      throw ApiException.of(ErrorCode.KAKAO_UNAVAILABLE);
    }
    if (response.path("app_id").longValue() != properties.appId()) {
      throw ApiException.of(ErrorCode.AUTH_KAKAO_TOKEN_INVALID);
    }
    return response.path("id").longValue();
  }
}
