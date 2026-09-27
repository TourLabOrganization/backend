package com.tourlab.api.domain.place.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "장소 목록")
public record PlaceListResponse(
    @Schema(description = "조건에 맞는 전체 장소 수. limit을 줘도 이 값은 줄지 않는다", example = "3118") int count,
    @Schema(description = "장소 목록") List<PlaceResponse> places) {}
