package com.tourlab.api.domain.itinerary.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "일정 합계(분)")
public record ItineraryTotalsResponse(
    @Schema(description = "체류 합", example = "410") Integer stayMin,
    @Schema(description = "이동 합", example = "144") Integer moveMin,
    @Schema(description = "대기 합", example = "0") Integer waitMin,
    @Schema(description = "총합", example = "554") Integer totalMin) {}
