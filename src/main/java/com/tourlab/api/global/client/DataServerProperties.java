package com.tourlab.api.global.client;

import jakarta.validation.constraints.NotBlank;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

// data-server(FastAPI) 접속 설정. 사설 IP라 VPC 안에서만 닿는다.
@Validated
@ConfigurationProperties(prefix = "data-server")
public record DataServerProperties(
    @NotBlank String baseUrl, Duration connectTimeout, Duration readTimeout) {

  public DataServerProperties {
    // data-server가 죽거나 느려도 이쪽 스레드까지 묶이지 않게 기본값을 짧게 잡는다.
    connectTimeout = connectTimeout == null ? Duration.ofSeconds(3) : connectTimeout;
    readTimeout = readTimeout == null ? Duration.ofSeconds(10) : readTimeout;
  }
}
