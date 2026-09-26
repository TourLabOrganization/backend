package com.tourlab.api.domain.insight.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Map;

@Schema(description = "지역×테마 강도 지수(TFI). 데이터랩 지역별 현황에서 계산한다")
public record TfiResponse(
    @Schema(description = "테마 키 목록") List<String> themes,
    @Schema(description = "테마 키 → 한글 이름") Map<String, String> themeLabels,
    @Schema(description = "계산된 지역 목록") List<String> regions,
    @Schema(description = "지역 → (테마 → 0~1 강도)") Map<String, Map<String, Double>> tfi) {}
