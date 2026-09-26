package com.tourlab.api.domain.insight.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "지역 체류시간")
public record StayTimeRegionResponse(
    @Schema(description = "지역", example = "강릉") String region,
    @Schema(description = "행정 단위", example = "기초") String tier,
    @Schema(description = "합산한 하위 행정구역 수", example = "1") Integer subUnits,
    @Schema(description = "합산한 하위 행정구역 이름") List<String> subUnitNames,
    @Schema(description = "방문당 체류시간(분)", example = "1715.0") Double stayMinutes,
    @Schema(description = "숙박일수", example = "2.54") Double lodgingDays,
    @Schema(description = "전국 대비 지수. 1.0이 전국 평균", example = "1.419") Double index,
    @Schema(description = "앱에 등록된 장소 수", example = "16") Integer appPlaces,
    @Schema(description = "앱 장소 체류시간 합(분)", example = "1100") Integer appStayMinSum,
    @Schema(description = "앱 장소 평균 체류시간(분)", example = "68.8") Double appStayMinMean,
    @Schema(description = "앱 장소를 다 보는 데 필요한 방문 횟수", example = "0.64") Double visitsToSeeAll) {}
