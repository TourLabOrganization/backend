package com.tourlab.api.domain.place.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "장소")
public record PlaceResponse(
    @Schema(description = "장소 식별자", example = "gj1") String id,
    @Schema(description = "원본 코스 블록", example = "gyeongju") String block,
    @Schema(description = "이름(한국어)", example = "효우당") String nameKo,
    @Schema(description = "이름(영어)", example = "Hyowoodang") String nameEn,
    @Schema(description = "지역", example = "경주") String region,
    @Schema(description = "앱 원본 분류. 오분류가 있어 그대로 믿지 않는다", example = "stay") String catApp,
    @Schema(description = "위도", example = "35.8923") Double lat,
    @Schema(description = "경도", example = "129.1786") Double lng,
    @Schema(description = "권장 체류 시간(분). 산출 근거가 기록돼 있지 않다", example = "180") Integer stayMin,
    @Schema(description = "운영시간 안내", example = "15:00 IN / 11:00 OUT") String hours,
    @Schema(description = "관련 영상 ID", example = "4m9eLr-NofA") String youtubeId,
    @Schema(description = "좌표 출처", example = "현곡면 소현효자길 21-3 지오코딩 확인") String sourceKo,
    @Schema(description = "코스 순번에서 빠지는 장소인지") Boolean inactive,
    @Schema(description = "최종 분류", example = "stay") String catFinal,
    @Schema(description = "최종 분류의 출처. tourapi면 공식 분류로 재분류된 것이고, app이면 앱 원본 값 그대로다", example = "app")
        String catSource,
    @Schema(description = "최종 분류 한글 표기", example = "숙박") String catFinalKo) {}
