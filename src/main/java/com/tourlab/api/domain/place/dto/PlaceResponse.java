package com.tourlab.api.domain.place.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "장소")
public record PlaceResponse(
    @Schema(description = "장소 식별자", example = "gj3") String id,
    @Schema(description = "원본 코스 블록", example = "gyeongju") String block,
    @Schema(description = "목록 정렬 키. 언어와 무관하게 고정이다", example = "2") Integer order,
    @Schema(description = "화면에 붙이는 번호", example = "3") Integer listNo,
    @Schema(description = "이름(한국어)", example = "동궁과 월지") String nameKo,
    @Schema(description = "이름(영어)", example = "Donggung & Wolji") String nameEn,
    @Schema(description = "지역", example = "경주") String region,
    @Schema(description = "지역(영어)", example = "Gyeongju") String regionEn,
    @Schema(description = "앱 원본 분류", example = "herit") String catApp,
    @Schema(description = "최종 분류", example = "herit") String catFinal,
    @Schema(description = "최종 분류의 출처. tourapi면 공식 분류로 재분류된 것이다", example = "app") String catSource,
    @Schema(description = "최종 분류 한글 표기", example = "문화유산·전통체험") String catFinalKo,
    @Schema(description = "위도", example = "35.8348") Double lat,
    @Schema(description = "경도", example = "129.2266") Double lng,
    @Schema(description = "권장 체류 시간(분)", example = "70") Integer stayMin,
    @Schema(description = "운영시간 안내", example = "09:00–22:00") String hours,
    @Schema(description = "설명(한국어)") String descKo,
    @Schema(description = "설명(영어)") String descEn,
    @Schema(description = "관련 영상 ID", example = "4m9eLr-NofA") String youtubeId,
    @Schema(description = "좌표 출처") String sourceKo,
    @Schema(description = "좌표 출처(영어)") String sourceEn,
    @Schema(description = "이미지 주소") String imageUrl,
    @Schema(description = "이미지 출처. 저작권 표기에 쓴다", example = "Wikimedia Commons") String imageCredit,
    @Schema(description = "카카오맵 주소") String kakaoUrl,
    @Schema(description = "한국관광 100선") Boolean k100,
    @Schema(description = "유네스코 등재") Boolean unesco,
    @Schema(description = "무장애 편의 제공") Boolean barrierFree,
    @Schema(description = "권역") String zone,
    @Schema(description = "시티투어 경유") Boolean cityTour,
    @Schema(description = "연관 장소 여부") Boolean related,
    @Schema(description = "코스 순번에서 빠지는 장소인지") Boolean inactive,
    @Schema(
            description =
                "데이터랩 인기관광지 순위. 받아둔 6개 지역 173곳(5.5%)에만 있다."
                    + " 배지로만 쓰고 정렬 키로는 쓰지 않는다. 정렬은 order로 한다",
            example = "3")
        Integer popRank,
    @Schema(description = "이 장소가 속한 코스 목록") List<PlaceCourseRefResponse> courses) {}
