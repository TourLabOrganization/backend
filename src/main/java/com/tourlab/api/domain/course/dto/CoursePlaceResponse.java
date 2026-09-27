package com.tourlab.api.domain.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "코스에 속한 장소")
public record CoursePlaceResponse(
    @Schema(description = "코스 안에서의 순번", example = "1") Integer seq,
    @Schema(description = "장소 식별자", example = "yw1") String placeId,
    @Schema(description = "코스-장소 식별자", example = "yw1") String coursePlaceId,
    @Schema(description = "원본 코스 블록", example = "yeongwol") String block,
    @Schema(description = "이름(한국어)", example = "청령포") String nameKo,
    @Schema(description = "이름(영어)", example = "Cheongnyeongpo") String nameEn,
    @Schema(description = "지역", example = "영월") String region,
    @Schema(description = "앱 원본 분류", example = "herit") String catApp,
    @Schema(description = "최종 분류", example = "herit") String catFinal,
    @Schema(description = "최종 분류 한글 표기", example = "역사·문화") String catFinalKo,
    @Schema(description = "위도", example = "37.175028") Double lat,
    @Schema(description = "경도", example = "128.444056") Double lng,
    @Schema(description = "권장 체류 시간(분)", example = "90") Integer stayMin,
    @Schema(description = "운영시간 안내") String hours,
    @Schema(description = "관련 영상 ID", example = "sc1") String youtubeId,
    @Schema(description = "작품 이름", example = "왕과 사는 남자 (2026)") String videoTitle,
    @Schema(description = "영상에서 이 장소가 어떤 장면인지", example = "강을 건너 유배지로 들어가다") String sceneKo) {}
