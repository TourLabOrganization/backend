package com.tourlab.api.global.exception;

import com.tourlab.api.global.response.ErrorCode;
import com.tourlab.api.global.response.ErrorResult;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  // 비즈니스 로직에서 명시적으로 던진 예외
  @ExceptionHandler(ApiException.class)
  public ResponseEntity<ErrorResult> handleApiException(ApiException e) {
    ErrorCode errorCode = e.getErrorCode();
    log.warn("[비즈니스 예외] code={}, message={}", errorCode.getCode(), errorCode.getMessage());
    return ErrorResult.of(errorCode);
  }

  // @Valid @RequestBody 검증 실패
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResult> handleMethodArgumentNotValid(
      MethodArgumentNotValidException e) {
    log.warn("[검증 실패] 요청 바디 검증 실패: {}", extractBindingErrors(e.getBindingResult()));
    return ErrorResult.of(ErrorCode.INVALID_INPUT_VALUE);
  }

  // @ModelAttribute 바인딩/검증 실패
  @ExceptionHandler(BindException.class)
  public ResponseEntity<ErrorResult> handleBindException(BindException e) {
    log.warn("[검증 실패] 파라미터 바인딩 검증 실패: {}", extractBindingErrors(e.getBindingResult()));
    return ErrorResult.of(ErrorCode.INVALID_INPUT_VALUE);
  }

  // @RequestParam/@PathVariable 등 메서드 파라미터 검증 실패
  @ExceptionHandler(HandlerMethodValidationException.class)
  public ResponseEntity<ErrorResult> handleHandlerMethodValidation(
      HandlerMethodValidationException e) {
    log.warn("[검증 실패] 메서드 파라미터 검증 실패: {}", e.getMessage());
    return ErrorResult.of(ErrorCode.INVALID_INPUT_VALUE);
  }

  // 요청 값 타입 불일치
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResult> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
    Class<?> requiredType = e.getRequiredType();
    log.warn(
        "[타입 불일치] name={}, requiredType={}",
        e.getName(),
        requiredType != null ? requiredType.getSimpleName() : "unknown");
    return ErrorResult.of(ErrorCode.INVALID_TYPE_VALUE);
  }

  // 필수 요청 파라미터 누락
  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<ErrorResult> handleMissingParameter(
      MissingServletRequestParameterException e) {
    log.warn("[파라미터 누락] name={}, type={}", e.getParameterName(), e.getParameterType());
    return ErrorResult.of(ErrorCode.MISSING_REQUEST_PARAMETER);
  }

  // 필수 요청 헤더 누락
  @ExceptionHandler(MissingRequestHeaderException.class)
  public ResponseEntity<ErrorResult> handleMissingHeader(MissingRequestHeaderException e) {
    log.warn("[헤더 누락] name={}", e.getHeaderName());
    return ErrorResult.of(ErrorCode.MISSING_REQUEST_HEADER);
  }

  // 요청 본문 파싱 실패 (JSON 문법 오류 등)
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResult> handleMessageNotReadable(HttpMessageNotReadableException e) {
    log.warn("[본문 파싱 실패] {}", e.getMessage());
    return ErrorResult.of(ErrorCode.MALFORMED_REQUEST_BODY);
  }

  // 지원하지 않는 HTTP 메서드
  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ErrorResult> handleMethodNotAllowed(
      HttpRequestMethodNotSupportedException e) {
    log.warn("[허용되지 않은 메서드] method={}, supported={}", e.getMethod(), e.getSupportedHttpMethods());
    return ErrorResult.of(ErrorCode.METHOD_NOT_ALLOWED);
  }

  // 지원하지 않는 Content-Type
  @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
  public ResponseEntity<ErrorResult> handleMediaTypeNotSupported(
      HttpMediaTypeNotSupportedException e) {
    log.warn(
        "[지원하지 않는 Content-Type] contentType={}, supported={}",
        e.getContentType(),
        e.getSupportedMediaTypes());
    return ErrorResult.of(ErrorCode.UNSUPPORTED_MEDIA_TYPE);
  }

  // 매핑되는 핸들러/리소스 없음 (404)
  @ExceptionHandler({NoHandlerFoundException.class, NoResourceFoundException.class})
  public ResponseEntity<ErrorResult> handleNotFound(Exception e) {
    log.warn("[리소스 없음] {}", e.getMessage());
    return ErrorResult.of(ErrorCode.RESOURCE_NOT_FOUND);
  }

  // 잘못된 인자
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResult> handleIllegalArgument(IllegalArgumentException e) {
    log.warn("[잘못된 인자] {}", e.getMessage());
    return ErrorResult.of(ErrorCode.BAD_REQUEST);
  }

  // 그 외 처리되지 않은 모든 예외
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResult> handleException(Exception e) {
    log.error("[처리되지 않은 예외] {}", e.getMessage(), e);
    return ErrorResult.of(ErrorCode.INTERNAL_SERVER_ERROR);
  }

  private String extractBindingErrors(BindingResult bindingResult) {
    return bindingResult.getFieldErrors().stream()
        .map(error -> "%s (%s)".formatted(error.getField(), error.getDefaultMessage()))
        .collect(Collectors.joining(", "));
  }
}
