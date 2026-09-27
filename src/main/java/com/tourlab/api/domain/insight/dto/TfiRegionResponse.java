package com.tourlab.api.domain.insight.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Map;

// 지역 하나만 물었을 때의 응답. 전체 조회와 tfi 모양이 다르다.
// 전체는 tfi[지역][테마], 여기서는 tfi[테마] 다.
@Schema(description = "지역 하나의 테마 강도와 산출 근거")
public record TfiRegionResponse(
    @Schema(description = "지역", example = "경주") String region,
    @Schema(description = "테마 키 목록") List<String> themes,
    @Schema(description = "테마 → 0~1 강도", example = "{\"herit\": 1.0, \"heal\": 0.0}")
        Map<String, Double> tfi,
    @Schema(
            description =
                "강도를 만든 원자료. 인기관광지 분류 구성(spotShare)·관광소비 비중"
                    + "(consumptionShare)·성연령별 소비(age_consumption)·유입유출(flows) 등이"
                    + " 지역마다 다른 키로 들어와 모양을 고정하지 않는다")
        Map<String, Object> detail) {}
