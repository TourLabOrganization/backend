package com.tourlab.api.domain.place.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "장소 목록")
public record PlaceListResponse(
    @Schema(description = "장소 수", example = "1171") int count,
    @Schema(description = "장소 목록") List<PlaceResponse> places) {}
