package com.tourlab.api.global.client;

import com.tourlab.api.global.exception.ApiException;
import com.tourlab.api.global.response.ErrorCode;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

// data-server 호출 창구. 응답 타입은 부르는 쪽(도메인)이 정한다.
@Component
public class DataServerClient {

  private static final Logger log = LoggerFactory.getLogger(DataServerClient.class);

  private final RestClient restClient;

  public DataServerClient(DataServerProperties properties) {
    SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
    factory.setConnectTimeout(properties.connectTimeout());
    factory.setReadTimeout(properties.readTimeout());
    this.restClient =
        RestClient.builder().baseUrl(properties.baseUrl()).requestFactory(factory).build();
  }

  public <T> T get(String path, Map<String, String> query, Class<T> type) {
    return call(
        path,
        () ->
            restClient
                .get()
                .uri(
                    uriBuilder -> {
                      uriBuilder.path(path);
                      // 값이 없는 파라미터는 붙이지 않는다. data-server는 생략하면 전체를 준다.
                      query.forEach(
                          (key, value) -> {
                            if (value != null && !value.isBlank()) {
                              uriBuilder.queryParam(key, value);
                            }
                          });
                      return uriBuilder.build();
                    })
                .retrieve()
                .body(type));
  }

  public <T> T post(String path, Object body, Class<T> type) {
    return call(path, () -> restClient.post().uri(path).body(body).retrieve().body(type));
  }

  private <T> T call(String path, java.util.function.Supplier<T> request) {
    try {
      T body = request.get();
      if (body == null) {
        throw ApiException.of(ErrorCode.DATA_SERVER_UNAVAILABLE);
      }
      return body;
    } catch (RestClientException e) {
      // 주소·포트·보안그룹 문제인지 data-server 자체 오류인지는 로그로만 남기고
      // 클라이언트에는 같은 코드로 내려준다.
      log.error("data-server 호출 실패: {}", path, e);
      throw ApiException.of(ErrorCode.DATA_SERVER_UNAVAILABLE);
    }
  }
}
