package com.tourlab.api.global.config;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.tourlab.api.global.annotation.ApiErrorCodeExample;
import com.tourlab.api.global.annotation.ApiErrorCodeExamples;
import com.tourlab.api.global.response.ErrorCode;
import com.tourlab.api.global.response.ErrorResult;
import com.tourlab.api.global.response.SuccessCode;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.converter.ResolvedSchema;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;

@Configuration
public class SwaggerConfig {

  private static final Set<String> NULL_TYPE = Set.of("null");
  private static final String SCHEMA_REF_PREFIX = "#/components/schemas/";
  private static final String ERROR_RESULT_SCHEMA = "ErrorResult";
  private static final String CODE_PROPERTY = "code";
  private static final String MESSAGE_PROPERTY = "message";

  private static final Map<String, SuccessCode> SUCCESS_CODES_BY_STATUS =
      Arrays.stream(SuccessCode.values())
          .filter(successCode -> successCode.getHttpStatus() != HttpStatus.OK)
          .collect(
              Collectors.toMap(
                  successCode -> String.valueOf(successCode.getHttpStatus().value()),
                  Function.identity(),
                  (first, second) -> first,
                  LinkedHashMap::new));

  @Bean
  public OpenAPI openAPI() {
    return new OpenAPI()
        .info(new Info().title("API").description("백엔드 API 문서").version("v1"))
        .components(
            new Components()
                .addSecuritySchemes(
                    "bearerAuth",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")))
        .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
  }

  @Bean
  public OperationCustomizer customize() {
    return (Operation operation, HandlerMethod handlerMethod) -> {
      ApiErrorCodeExamples multi = handlerMethod.getMethodAnnotation(ApiErrorCodeExamples.class);
      if (multi != null) {
        generateErrorCodeResponseExample(operation, multi.value());
        return operation;
      }

      ApiErrorCodeExample single = handlerMethod.getMethodAnnotation(ApiErrorCodeExample.class);
      if (single != null) {
        generateErrorCodeResponseExample(operation, new ErrorCode[] {single.value()});
      }

      return operation;
    };
  }

  @Bean
  public OpenApiCustomizer nullableObjectSchemaCustomizer() {
    return openApi -> {
      Components components = openApi.getComponents();
      if (components == null || components.getSchemas() == null) {
        return;
      }
      components.getSchemas().values().forEach(this::restoreNullableObjectType);
    };
  }

  @Bean
  public OpenApiCustomizer errorResultSchemaCustomizer() {
    return openApi -> {
      Components components = openApi.getComponents();
      ResolvedSchema resolved =
          ModelConverters.getInstance(true).readAllAsResolvedSchema(ErrorResult.class);
      if (components == null || resolved == null || resolved.schema == null) {
        return;
      }
      components.addSchemas(ERROR_RESULT_SCHEMA, resolved.schema);
    };
  }

  @Bean
  public OpenApiCustomizer successCodeSchemaCustomizer() {
    return openApi -> {
      Components components = openApi.getComponents();
      if (openApi.getPaths() == null || components == null || components.getSchemas() == null) {
        return;
      }
      openApi.getPaths().values().stream()
          .flatMap(pathItem -> pathItem.readOperations().stream())
          .forEach(operation -> applySuccessCodeExample(operation, components));
    };
  }

  private void applySuccessCodeExample(Operation operation, Components components) {
    ApiResponses responses = operation.getResponses();
    if (responses == null) {
      return;
    }

    SUCCESS_CODES_BY_STATUS.forEach(
        (status, successCode) -> {
          ApiResponse response = responses.get(status);
          if (response == null || response.getContent() == null) {
            return;
          }
          response
              .getContent()
              .values()
              .forEach(mediaType -> pointToSuccessCodeSchema(mediaType, successCode, components));
        });
  }

  private void pointToSuccessCodeSchema(
      MediaType mediaType, SuccessCode successCode, Components components) {
    Schema<?> schema = mediaType.getSchema();
    if (schema == null || schema.get$ref() == null) {
      return;
    }

    String ref = schema.get$ref();
    String wrapperName = ref.substring(ref.lastIndexOf('/') + 1);
    Schema<?> wrapper = components.getSchemas().get(wrapperName);
    if (wrapper == null || wrapper.getProperties() == null) {
      return;
    }

    String duplicateName = wrapperName + toPascalCase(successCode.name());
    if (!components.getSchemas().containsKey(duplicateName)) {
      components.addSchemas(duplicateName, duplicateWithSuccessCode(wrapper, successCode));
    }
    mediaType.setSchema(new Schema<>().$ref(SCHEMA_REF_PREFIX + duplicateName));
  }

  private Schema<?> duplicateWithSuccessCode(Schema<?> wrapper, SuccessCode successCode) {
    Map<String, Schema> properties = new LinkedHashMap<>(wrapper.getProperties());
    properties.computeIfPresent(
        CODE_PROPERTY, (name, property) -> withExample(property, successCode.getCode()));
    properties.computeIfPresent(
        MESSAGE_PROPERTY, (name, property) -> withExample(property, successCode.getMessage()));

    Schema<Object> duplicate = new Schema<>();
    duplicate.setTypes(copyOf(wrapper.getTypes()));
    duplicate.setDescription(wrapper.getDescription());
    duplicate.setRequired(
        wrapper.getRequired() == null ? null : new ArrayList<>(wrapper.getRequired()));
    duplicate.setProperties(properties);
    return duplicate;
  }

  private Schema<?> withExample(Schema<?> property, Object example) {
    Schema<Object> copy = new Schema<>();
    copy.setTypes(copyOf(property.getTypes()));
    copy.setFormat(property.getFormat());
    copy.setDescription(property.getDescription());
    copy.setExample(example);
    return copy;
  }

  private Set<String> copyOf(Set<String> types) {
    return types == null ? null : new LinkedHashSet<>(types);
  }

  private String toPascalCase(String enumName) {
    return Arrays.stream(enumName.split("_"))
        .map(word -> word.charAt(0) + word.substring(1).toLowerCase())
        .collect(Collectors.joining());
  }

  private void restoreNullableObjectType(Schema<?> schema) {
    if (schema == null) {
      return;
    }

    if (schema.get$ref() != null) {
      if (NULL_TYPE.equals(schema.getTypes())) {
        schema.setTypes(new LinkedHashSet<>(List.of("object", "null")));
      }
      return;
    }

    if (schema.getProperties() != null) {
      schema.getProperties().values().forEach(this::restoreNullableObjectType);
    }
    restoreNullableObjectType(schema.getItems());
    if (schema.getAdditionalProperties() instanceof Schema<?> additionalProperties) {
      restoreNullableObjectType(additionalProperties);
    }
  }

  private void generateErrorCodeResponseExample(Operation operation, ErrorCode[] errorCodes) {
    ApiResponses responses = operation.getResponses();
    if (responses == null) {
      responses = new ApiResponses();
      operation.setResponses(responses);
    }

    Map<HttpStatus, List<ErrorCode>> groupedByStatus =
        Arrays.stream(errorCodes)
            .collect(
                Collectors.groupingBy(
                    ErrorCode::getHttpStatus, LinkedHashMap::new, Collectors.toList()));

    ApiResponses target = responses;
    groupedByStatus.forEach(
        (httpStatus, codes) ->
            target.addApiResponse(
                String.valueOf(httpStatus.value()),
                new ApiResponse()
                    .description(httpStatus.getReasonPhrase())
                    .content(
                        new Content().addMediaType(APPLICATION_JSON_VALUE, toMediaType(codes)))));
  }

  private MediaType toMediaType(List<ErrorCode> errorCodes) {
    MediaType mediaType = new MediaType();
    mediaType.setSchema(new Schema<>().$ref(SCHEMA_REF_PREFIX + ERROR_RESULT_SCHEMA));
    errorCodes.forEach(
        errorCode -> mediaType.addExamples(errorCode.getCode(), toExample(errorCode)));
    return mediaType;
  }

  private Example toExample(ErrorCode errorCode) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("code", errorCode.getCode());
    body.put("message", errorCode.getMessage());
    return new Example().summary(errorCode.getMessage()).value(body);
  }
}
