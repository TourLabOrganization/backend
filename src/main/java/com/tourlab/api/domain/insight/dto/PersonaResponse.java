package com.tourlab.api.domain.insight.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Map;

@Schema(description = "미리 계산해 둔 사용자 유형")
public record PersonaResponse(
    @Schema(description = "관심 카테고리 이름. interests 인덱스가 이 순서를 가리킨다") List<String> cats,
    @Schema(description = "유형 이름 → 군집 코드(C1~C10)") Map<String, String> clusters,
    // 한 줄은 [테마이름, fit, interest, region, score] 순서의 혼합 배열이다.
    @Schema(description = "유형 이름 → 테마 순위. 한 줄은 [테마, fit, interest, region, score]")
        Map<String, List<List<Object>>> rankings) {}
