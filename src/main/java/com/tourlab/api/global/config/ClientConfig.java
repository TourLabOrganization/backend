package com.tourlab.api.global.config;

import com.tourlab.api.global.client.DataServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

// 외부 서버 호출 설정.
@Configuration
@EnableConfigurationProperties(DataServerProperties.class)
public class ClientConfig {}
