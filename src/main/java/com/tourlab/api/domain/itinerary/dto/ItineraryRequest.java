package com.tourlab.api.domain.itinerary.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

// 주지 않은 값은 null로 보내지 않고 아예 뺀다. data-server가 정수·문자열을 기대하는
// 자리에 null이 가면 422로 거절한다.
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "일정표 요청. courseId 또는 placeIds 중 하나를 준다")
public record ItineraryRequest(
    @Schema(description = "코스 id. 주면 그 코스의 장소를 seq 순서로 쓴다", example = "kings-warden-route")
        String courseId,
    @Schema(description = "장소 id 목록. courseId 대신 쓴다. 주는 순서가 곧 동선이다") List<String> placeIds,
    @Schema(description = "여행 일수", example = "1", defaultValue = "1") Integer days,
    @Schema(description = "이동수단: transit · driving · own", example = "transit") String mode,
    @Schema(description = "출발지 출발 시각", example = "08:00") String depTime,
    @Schema(description = "여행지 출발(귀가) 시각", example = "19:00") String retTime,
    @Schema(description = "출발지에서 지역 관문까지 광역 이동(분). 첫날 일정 창을 줄인다", example = "0")
        Integer accessMin) {}
