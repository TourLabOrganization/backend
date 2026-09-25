package com.tourlab.api.domain.place.entity;

import com.tourlab.api.global.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 지도에 찍히는 장소 한 곳. 코스에 속하는지와 무관하게 장소 자체의 사실만 담는다.
@Getter
@Entity
@Table(
    name = "places",
    indexes = {
      @Index(name = "ux_places_code", columnList = "code", unique = true),
      @Index(name = "ix_places_region_ko", columnList = "region_ko"),
      @Index(name = "ix_places_category", columnList = "category")
    })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Place extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // 프로토타입이 쓰던 문자열 식별자('jd1', 'gjx12').
  // ETL을 다시 돌려도 같은 장소를 찾아내려면 필요하다.
  @Column(nullable = false, length = 40)
  private String code;

  @Column(name = "name_ko", nullable = false, length = 120)
  private String nameKo;

  @Column(name = "name_en", nullable = false, length = 200)
  private String nameEn;

  @Column(precision = 9, scale = 7)
  private BigDecimal lat;

  @Column(precision = 10, scale = 7)
  private BigDecimal lng;

  // 좌표를 어디서 얻었는지. '주소 기반 좌표' 처럼 사람이 읽는 문장이다.
  // 공모전 제출물에서 출처를 대야 하므로 버리지 않는다.
  @Column(name = "coord_source_ko", length = 300)
  private String coordSourceKo;

  @Column(name = "coord_source_en", length = 300)
  private String coordSourceEn;

  // 데이터랩 지역 표기와 맞추는 단위('경주', '서울'). 프로토타입에서 59%만 채워져 있다.
  @Column(name = "region_ko", length = 40)
  private String regionKo;

  @Column(name = "region_en", length = 80)
  private String regionEn;

  @Enumerated(EnumType.STRING)
  @Column(length = 20)
  private PlaceCategory category;

  // 권장 체류 시간(분). 프로토타입 값은 전부 10분 단위이고 산출 근거가 기록돼 있지 않다.
  // 재산정 전까지 정렬·일정 계산의 근거로 삼지 않는다.
  @Column(name = "dwell_minutes")
  private Integer dwellMinutes;

  // '07:00–20:00 · 어른 5,000원 · 첫째 월요일 휴무' 같은 한 줄 안내.
  // 구조가 제각각이라 파싱하지 않고 그대로 보여준다.
  @Column(name = "opening_hours", length = 300)
  private String openingHours;

  @Column(name = "description_ko", columnDefinition = "text")
  private String descriptionKo;

  @Column(name = "description_en", columnDefinition = "text")
  private String descriptionEn;

  @Column(name = "image_url", length = 500)
  private String imageUrl;

  // 이미지 출처. 저작권 표기 의무가 있어 이미지와 함께가 아니면 저장하지 않는다.
  @Column(name = "image_credit", length = 200)
  private String imageCredit;

  // 상세 화면 배지로 쓰는 표시들.
  @Column(nullable = false)
  private boolean unesco;

  @Column(name = "barrier_free", nullable = false)
  private boolean barrierFree;

  @Column(name = "korea_top100", nullable = false)
  private boolean koreaTop100;

  public Place(String code, String nameKo, String nameEn) {
    this.code = code;
    this.nameKo = nameKo;
    this.nameEn = nameEn;
  }

  public void updateCoordinates(
      BigDecimal lat, BigDecimal lng, String coordSourceKo, String coordSourceEn) {
    this.lat = lat;
    this.lng = lng;
    this.coordSourceKo = coordSourceKo;
    this.coordSourceEn = coordSourceEn;
  }

  public void updateRegion(String regionKo, String regionEn) {
    this.regionKo = regionKo;
    this.regionEn = regionEn;
  }

  public void updateCategory(PlaceCategory category) {
    this.category = category;
  }

  public void updateVisitInfo(Integer dwellMinutes, String openingHours) {
    this.dwellMinutes = dwellMinutes;
    this.openingHours = openingHours;
  }

  public void updateDescription(String descriptionKo, String descriptionEn) {
    this.descriptionKo = descriptionKo;
    this.descriptionEn = descriptionEn;
  }

  // 출처 없는 이미지는 쓸 수 없으므로 둘을 따로 넣지 못하게 한다.
  public void updateImage(String imageUrl, String imageCredit) {
    this.imageUrl = imageUrl;
    this.imageCredit = imageCredit;
  }

  public void updateBadges(boolean unesco, boolean barrierFree, boolean koreaTop100) {
    this.unesco = unesco;
    this.barrierFree = barrierFree;
    this.koreaTop100 = koreaTop100;
  }

  public boolean hasCoordinates() {
    return lat != null && lng != null;
  }
}
