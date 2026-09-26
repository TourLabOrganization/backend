package com.tourlab.api.domain.insight.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "테마 추천 결과")
public record RecommendResponse(
    @Schema(description = "요청한 군집 코드", example = "C4") String cluster,
    @Schema(description = "관심 카테고리 이름") List<String> cats,
    @Schema(description = "점수에 반영된 지역. 요청에 region을 주지 않으면 비어 있다", example = "경주") String region,
    @Schema(description = "지역 보정이 실제로 적용됐는지") boolean regionApplied,
    @Schema(
            description = "점수 산출에 쓰인 자료 출처. 데이터랩 반영 여부를 여기서 확인한다",
            example = "[\"국민여행조사\", \"한국관광 데이터랩 (지역×테마 강도 TFI)\"]")
        List<String> sources,
    @Schema(description = "점수 내림차순 테마 목록") List<RecommendThemeResponse> themes) {}
