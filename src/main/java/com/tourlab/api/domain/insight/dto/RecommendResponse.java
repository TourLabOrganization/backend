package com.tourlab.api.domain.insight.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "테마 추천 결과")
public record RecommendResponse(
    @Schema(description = "요청한 군집 코드", example = "C4") String cluster,
    @Schema(description = "관심 카테고리 이름") List<String> cats,
    @Schema(description = "지역 보정이 실제로 적용됐는지") boolean regionApplied,
    @Schema(description = "점수 내림차순 테마 목록") List<RecommendThemeResponse> themes) {}
