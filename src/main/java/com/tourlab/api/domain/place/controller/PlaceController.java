package com.tourlab.api.domain.place.controller;

import com.tourlab.api.domain.place.dto.PlaceListResponse;
import com.tourlab.api.global.annotation.ApiErrorCodeExamples;
import com.tourlab.api.global.client.DataServerClient;
import com.tourlab.api.global.response.ApiResult;
import com.tourlab.api.global.response.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import java.util.LinkedHashMap;
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
          "장소 3,118곳을 반환한다. 전체는 3MB라 region·course 로 거르거나 offset·limit 으로 끊어 받는다."
              + " 목록 정렬은 order 를 쓴다. 이름으로 정렬하면 한국어·영어 순서가 갈린다."
              + " popRank 는 6개 지역 173곳(5.5%)에만 있어 정렬 키로 쓰면 나머지가 한 덩어리로 밀린다."
              + " 배지 표시에만 쓴다.")
  @ApiErrorCodeExamples({ErrorCode.INVALID_INPUT_VALUE, ErrorCode.DATA_SERVER_UNAVAILABLE})
  @GetMapping
  public ResponseEntity<ApiResult<PlaceListResponse>> getPlaces(
      @Parameter(description = "지역 이름", example = "경주") @RequestParam(required = false)
          String region,
      @Parameter(description = "코스 식별자", example = "rescene-route") @RequestParam(required = false)
          String course,
      @Parameter(description = "건너뛸 개수", example = "0") @RequestParam(required = false) @Min(0)
          Integer offset,
      @Parameter(description = "받을 개수", example = "50") @RequestParam(required = false) @Min(1)
          Integer limit) {
    Map<String, String> query = new LinkedHashMap<>();
    put(query, "region", region);
    put(query, "course", course);
    put(query, "offset", offset);
    put(query, "limit", limit);
    return ApiResult.success(dataServerClient.get("/v1/places", query, PlaceListResponse.class));
  }

  private void put(Map<String, String> query, String key, Object value) {
    if (value != null) {
      query.put(key, value.toString());
    }
  }
}
