package com.tourlab.api.global.config;

import com.tourlab.api.domain.auth.client.KakaoProperties;
import com.tourlab.api.global.client.DataServerProperties;
import java.time.Duration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

// 외부 서버 호출 설정.
@Configuration
@EnableConfigurationProperties({DataServerProperties.class, KakaoProperties.class})
public class ClientConfig {

  @Bean
  public RestClient kakaoRestClient() {
    SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
    factory.setConnectTimeout(Duration.ofSeconds(3));
    factory.setReadTimeout(Duration.ofSeconds(5));
    return RestClient.builder().baseUrl("https://kapi.kakao.com").requestFactory(factory).build();
  }
}
