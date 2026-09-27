package com.tourlab.api.domain.itinerary.controller;

import com.tourlab.api.domain.itinerary.dto.ItineraryRequest;
import com.tourlab.api.domain.itinerary.dto.ItineraryResponse;
import com.tourlab.api.global.annotation.ApiErrorCodeExamples;
import com.tourlab.api.global.client.DataServerClient;
import com.tourlab.api.global.response.ApiResult;
import com.tourlab.api.global.response.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Itinerary", description = "일정표 API")
@RestController
@RequestMapping("/api/v1/itinerary")
@RequiredArgsConstructor
public class ItineraryController {

  private final DataServerClient dataServerClient;

  @Operation(
      summary = "일정표 생성",
      description =
          "코스나 장소 목록을 일자별 도착·출발 시각으로 만든다."
              + " 시간이 모자라 빠진 장소는 dropped 로 알려준다."
              + " 도착 전에 문을 닫는 곳은 해당 칸의 closesBefore 가 true 다.")
  @ApiErrorCodeExamples({ErrorCode.INVALID_INPUT_VALUE, ErrorCode.DATA_SERVER_UNAVAILABLE})
  @PostMapping
  public ResponseEntity<ApiResult<ItineraryResponse>> createItinerary(
      @Valid @RequestBody ItineraryRequest request) {
    return ApiResult.success(
        dataServerClient.post("/v1/itinerary", request, ItineraryResponse.class));
  }
}
