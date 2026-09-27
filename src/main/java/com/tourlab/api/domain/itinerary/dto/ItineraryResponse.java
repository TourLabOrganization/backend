package com.tourlab.api.domain.itinerary.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "일자별 도착·출발 시각")
public record ItineraryResponse(
    @Schema(description = "코스 식별자", example = "kings-warden-route") String courseId,
    @Schema(description = "여행 일수", example = "1") Integer days,
    @Schema(description = "이동수단", example = "transit") String mode,
    @Schema(description = "하루에 쓸 수 있는 시간(분). 일차 순서다", example = "[600]") List<Integer> dayWindows,
    @Schema(description = "동선을 다시 짰는지") Boolean reordered,
    @Schema(description = "일정에 넣은 장소 수", example = "5") Integer placed,
    @Schema(description = "시간이 모자라 뺀 장소 수. 0보다 크면 화면에서 알린다", example = "0") Integer dropped,
    @Schema(description = "합계") ItineraryTotalsResponse totals,
    @Schema(description = "일정표") List<ItineraryStopResponse> schedule) {}
