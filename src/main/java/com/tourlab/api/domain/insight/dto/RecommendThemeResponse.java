package com.tourlab.api.domain.insight.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;

@Schema(description = "추천된 테마 하나")
public record RecommendThemeResponse(
    @Schema(description = "테마 이름", example = "왕과 사는 남자") String theme,
    @Schema(description = "군집 적합도", example = "0.3133") Double fit,
    @Schema(description = "관심사 일치도", example = "0.3556") Double interest,
    @Schema(description = "지역 보정치", example = "0.0") Double region,
    @Schema(description = "최종 점수", example = "0.6689") Double score,
    @Schema(description = "카테고리별 구성 비율") Map<String, Double> share) {}
