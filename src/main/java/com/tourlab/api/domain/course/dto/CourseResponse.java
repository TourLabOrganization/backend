package com.tourlab.api.domain.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "영상 IP를 따라 도는 코스")
public record CourseResponse(
    @Schema(description = "코스 식별자", example = "kings-warden-route") String courseId,
    @Schema(description = "코스 이름", example = "왕과 사는 남자") String title,
    @Schema(description = "원본 프로토타입 파일", example = "Kings Warden Route.dc.html") String file,
    @Schema(description = "코스가 지나는 지역") List<String> regions,
    @Schema(description = "장소 수", example = "5") Integer count,
    @Schema(description = "체류 시간 합(분)", example = "410") Integer stayMinSum,
    @Schema(description = "장소 목록. seq 순서다. withPlaces=false면 내려오지 않는다")
        List<CoursePlaceResponse> places) {}
