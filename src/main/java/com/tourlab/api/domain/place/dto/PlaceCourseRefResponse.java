package com.tourlab.api.domain.place.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "장소가 속한 코스")
public record PlaceCourseRefResponse(
    @Schema(description = "코스 식별자", example = "rescene-route") String courseId,
    @Schema(description = "코스 이름", example = "RESCENE") String title,
    @Schema(description = "코스 안에서의 순번", example = "3") Integer seq) {}
