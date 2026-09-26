package com.tourlab.api.domain.insight.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "지역 체류시간과 전국 대비 지수")
public record StayTimeResponse(
    @Schema(description = "기준 연도", example = "2025") String latestYear,
    @Schema(description = "원자료 출처", example = "전국 다운로드 (2025년, 시군구 229개)") String source,
    @Schema(description = "지역 목록") List<StayTimeRegionResponse> regions) {}
