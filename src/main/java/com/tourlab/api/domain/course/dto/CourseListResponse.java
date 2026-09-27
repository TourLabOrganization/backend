package com.tourlab.api.domain.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "코스 목록")
public record CourseListResponse(
    @Schema(description = "코스 수", example = "5") int count,
    @Schema(description = "코스 목록") List<CourseResponse> courses) {}
