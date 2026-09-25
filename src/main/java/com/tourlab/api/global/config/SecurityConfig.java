package com.tourlab.api.global.config;

import com.tourlab.api.global.security.CorsProperties;
import com.tourlab.api.global.security.JwtProperties;
import com.tourlab.api.global.security.service.UserPrincipalJwtAuthenticationConverter;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableMethodSecurity
@EnableConfigurationProperties({JwtProperties.class, CorsProperties.class})
public class SecurityConfig {

  private static final String CLAIM_TYPE = "typ";
  private static final String TOKEN_TYPE_ACCESS = "access";

  private static final String[] PUBLIC_GET_PATHS = {
    "/health", "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**"
  };

  private static final String[] PUBLIC_POST_PATHS = {
    "/api/v1/auth/signup", "/api/v1/auth/login", "/api/v1/auth/reissue"
  };

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public JwtEncoder jwtEncoder(JwtProperties jwtProperties) {
    return new NimbusJwtEncoder(new ImmutableSecret<>(secretKey(jwtProperties)));
  }

  @Bean
  public JwtDecoder jwtDecoder(JwtProperties jwtProperties) {
    return NimbusJwtDecoder.withSecretKey(secretKey(jwtProperties))
        .macAlgorithm(MacAlgorithm.HS256)
        .build();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(
      HttpSecurity http,
      JwtProperties jwtProperties,
      AuthenticationEntryPoint authenticationEntryPoint,
      AccessDeniedHandler accessDeniedHandler,
      UserPrincipalJwtAuthenticationConverter userPrincipalJwtAuthenticationConverter,
      CorsConfigurationSource corsConfigurationSource)
      throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .cors(cors -> cors.configurationSource(corsConfigurationSource))
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(PUBLIC_GET_PATHS)
                    .permitAll()
                    .requestMatchers(HttpMethod.POST, PUBLIC_POST_PATHS)
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .exceptionHandling(
            handling ->
                handling
                    .authenticationEntryPoint(authenticationEntryPoint)
                    .accessDeniedHandler(accessDeniedHandler))
        .oauth2ResourceServer(
            oauth2 ->
                oauth2
                    .jwt(
                        jwt ->
                            jwt.decoder(accessTokenDecoder(jwtProperties))
                                .jwtAuthenticationConverter(
                                    userPrincipalJwtAuthenticationConverter))
                    .authenticationEntryPoint(authenticationEntryPoint)
                    .accessDeniedHandler(accessDeniedHandler));
    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource(CorsProperties corsProperties) {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(corsProperties.allowedOrigins());
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE"));
    config.setAllowedHeaders(List.of(HttpHeaders.AUTHORIZATION, HttpHeaders.CONTENT_TYPE));
    config.setAllowCredentials(true);
    config.setMaxAge(3600L);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/api/**", config);
    return source;
  }

  private NimbusJwtDecoder accessTokenDecoder(JwtProperties jwtProperties) {
    NimbusJwtDecoder decoder =
        NimbusJwtDecoder.withSecretKey(secretKey(jwtProperties))
            .macAlgorithm(MacAlgorithm.HS256)
            .build();
    decoder.setJwtValidator(
        new DelegatingOAuth2TokenValidator<>(
            JwtValidators.createDefault(), accessTokenTypeValidator()));
    return decoder;
  }

  private OAuth2TokenValidator<Jwt> accessTokenTypeValidator() {
    return jwt ->
        TOKEN_TYPE_ACCESS.equals(jwt.getClaimAsString(CLAIM_TYPE))
            ? OAuth2TokenValidatorResult.success()
            : OAuth2TokenValidatorResult.failure(
                new OAuth2Error("invalid_token", "access token이 아닙니다.", null));
  }

  private SecretKeySpec secretKey(JwtProperties jwtProperties) {
    return new SecretKeySpec(jwtProperties.secret().getBytes(StandardCharsets.UTF_8), "HmacSHA256");
  }
}
