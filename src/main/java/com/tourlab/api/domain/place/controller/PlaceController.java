package com.tourlab.api.domain.place.controller;

import com.tourlab.api.domain.place.dto.PlaceListResponse;
import com.tourlab.api.global.annotation.ApiErrorCodeExamples;
import com.tourlab.api.global.client.DataServerClient;
import com.tourlab.api.global.response.ApiResult;
import com.tourlab.api.global.response.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Collections;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Place", description = "장소 API")
@RestController
@RequestMapping("/api/v1/places")
@RequiredArgsConstructor
public class PlaceController {

  private final DataServerClient dataServerClient;

  @Operation(
      summary = "장소 목록 조회",
      description =
          "앱에 등록된 장소를 반환한다. region을 주면 그 지역만 거른다."
              + " 분류(catFinal)는 catSource가 tourapi인 것만 공식 분류로 재분류된 값이다.")
  @ApiErrorCodeExamples({ErrorCode.DATA_SERVER_UNAVAILABLE})
  @GetMapping
  public ResponseEntity<ApiResult<PlaceListResponse>> getPlaces(
      @Parameter(description = "지역 이름", example = "경주") @RequestParam(required = false)
          String region) {
    return ApiResult.success(
        dataServerClient.get(
            "/v1/places",
            region == null ? Collections.emptyMap() : Map.of("region", region),
            PlaceListResponse.class));
  }
}
