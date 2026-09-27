package com.tourlab.api.domain.course.controller;

import com.tourlab.api.domain.course.dto.CourseListResponse;
import com.tourlab.api.global.annotation.ApiErrorCodeExamples;
import com.tourlab.api.global.client.DataServerClient;
import com.tourlab.api.global.response.ApiResult;
import com.tourlab.api.global.response.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Course", description = "코스 API")
@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
public class CourseController {

  private final DataServerClient dataServerClient;

  @Operation(
      summary = "코스 목록 조회",
      description =
          "영상 IP를 따라 도는 코스와 그 장소를 seq 순서로 반환한다." + " 목록 화면처럼 장소가 필요 없으면 withPlaces=false 로 줄인다.")
  @ApiErrorCodeExamples({ErrorCode.DATA_SERVER_UNAVAILABLE})
  @GetMapping
  public ResponseEntity<ApiResult<CourseListResponse>> getCourses(
      @Parameter(description = "코스 식별자", example = "kings-warden-route")
          @RequestParam(required = false)
          String courseId,
      @Parameter(description = "장소까지 함께 받을지") @RequestParam(required = false) Boolean withPlaces) {
    Map<String, String> query = new LinkedHashMap<>();
    if (courseId != null) {
      query.put("courseId", courseId);
    }
    if (withPlaces != null) {
      query.put("withPlaces", withPlaces.toString());
    }
    return ApiResult.success(dataServerClient.get("/v1/courses", query, CourseListResponse.class));
  }
}
