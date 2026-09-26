package com.tourlab.api.domain.insight.controller;

import com.tourlab.api.domain.insight.dto.PersonaResponse;
import com.tourlab.api.domain.insight.dto.RecommendRequest;
import com.tourlab.api.domain.insight.dto.RecommendResponse;
import com.tourlab.api.domain.insight.dto.StayTimeResponse;
import com.tourlab.api.domain.insight.dto.TfiResponse;
import com.tourlab.api.global.annotation.ApiErrorCodeExamples;
import com.tourlab.api.global.client.DataServerClient;
import com.tourlab.api.global.response.ApiResult;
import com.tourlab.api.global.response.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Collections;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// 데이터랩 파이프라인 산출물. 계산은 전부 data-server에서 끝나 있고 여기서는 중계만 한다.
@Tag(name = "Insight", description = "데이터랩 분석 API")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class InsightController {

  private final DataServerClient dataServerClient;

  @Operation(summary = "지역×테마 강도(TFI) 조회", description = "데이터랩 지역별 현황으로 계산한 0~1 강도.")
  @ApiErrorCodeExamples({ErrorCode.DATA_SERVER_UNAVAILABLE})
  @GetMapping("/tfi")
  public ResponseEntity<ApiResult<TfiResponse>> getTfi(
      @Parameter(description = "지역 이름", example = "경주") @RequestParam(required = false)
          String region) {
    return ApiResult.success(dataServerClient.get("/v1/tfi", query(region), TfiResponse.class));
  }

  @Operation(summary = "지역 체류시간 조회", description = "데이터랩 방문당 체류시간과 전국 대비 지수.")
  @ApiErrorCodeExamples({ErrorCode.DATA_SERVER_UNAVAILABLE})
  @GetMapping("/staytime")
  public ResponseEntity<ApiResult<StayTimeResponse>> getStayTime(
      @Parameter(description = "지역 이름", example = "강릉") @RequestParam(required = false)
          String region) {
    return ApiResult.success(
        dataServerClient.get("/v1/staytime", query(region), StayTimeResponse.class));
  }

  @Operation(summary = "사용자 유형 조회", description = "미리 계산해 둔 유형 8종과 유형별 테마 순위.")
  @ApiErrorCodeExamples({ErrorCode.DATA_SERVER_UNAVAILABLE})
  @GetMapping("/personas")
  public ResponseEntity<ApiResult<PersonaResponse>> getPersonas() {
    return ApiResult.success(
        dataServerClient.get("/v1/personas", Collections.emptyMap(), PersonaResponse.class));
  }

  @Operation(summary = "테마 추천", description = "설문 응답으로 테마 순위를 계산한다.")
  @ApiErrorCodeExamples({ErrorCode.INVALID_INPUT_VALUE, ErrorCode.DATA_SERVER_UNAVAILABLE})
  @PostMapping("/recommend")
  public ResponseEntity<ApiResult<RecommendResponse>> recommend(
      @Valid @RequestBody RecommendRequest request) {
    return ApiResult.success(
        dataServerClient.post("/v1/recommend", request, RecommendResponse.class));
  }

  private Map<String, String> query(String region) {
    return region == null ? Collections.emptyMap() : Map.of("region", region);
  }
}
