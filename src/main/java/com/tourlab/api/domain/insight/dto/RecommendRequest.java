package com.tourlab.api.domain.insight.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

// 주지 않은 값은 null로 보내지 않고 아예 뺀다. data-server가 정수·문자열을 기대하는
// 자리에 null이 가면 422로 거절한다.
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "테마 추천 요청")
public record RecommendRequest(
    @Schema(description = "군집 코드. /api/v1/personas 의 clusters 값", example = "C4") @NotBlank
        String cluster,
    @Schema(description = "관심 카테고리 인덱스. /api/v1/personas 의 cats 순서", example = "[0, 1]")
        List<Integer> interests,
    @Schema(description = "야경 선호", defaultValue = "false") Boolean night,
    @Schema(description = "여행 지역. 주면 그 지역 TFI를 보정에 반영한다", example = "경주") String region) {

  // 원시 boolean으로 두면 JSON에서 생략했을 때 Jackson이 본문 파싱을 실패시킨다.
  public RecommendRequest {
    night = night != null && night;
  }
}
