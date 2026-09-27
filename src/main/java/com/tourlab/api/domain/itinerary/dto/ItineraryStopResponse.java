package com.tourlab.api.domain.itinerary.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "일정표의 한 칸")
public record ItineraryStopResponse(
    @Schema(description = "며칠째", example = "1") Integer day,
    @Schema(description = "그날의 순서", example = "1") Integer order,
    @Schema(description = "장소 식별자", example = "yw1") String placeId,
    @Schema(description = "이름(한국어)", example = "청령포") String nameKo,
    @Schema(description = "분류", example = "herit") String cat,
    @Schema(description = "앞 장소에서 오는 데 걸린 이동 시간(분)", example = "0") Integer moveMin,
    @Schema(description = "문 열 때까지 기다리는 시간(분)", example = "0") Integer waitMin,
    @Schema(description = "체류 시간(분)", example = "90") Integer stayMin,
    @Schema(description = "도착 시각", example = "09:00") String arrive,
    @Schema(description = "출발 시각", example = "10:30") String leave,
    @Schema(description = "도착 시각(자정 기준 분)", example = "540") Integer arriveMin,
    @Schema(description = "출발 시각(자정 기준 분)", example = "630") Integer leaveMin,
    @Schema(description = "도착 전에 문을 닫는 곳인지. true면 화면에서 경고한다") Boolean closesBefore) {}
