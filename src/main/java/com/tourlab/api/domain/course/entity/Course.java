package com.tourlab.api.domain.course.entity;

import com.tourlab.api.global.entity.BaseTimeEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 영상 한 편(또는 한 작품)을 따라 도는 코스. 프로토타입의 route 화면 하나에 해당한다.
@Getter
@Entity
@Table(
    name = "courses",
    indexes = {@Index(name = "ux_courses_code", columnList = "code", unique = true)})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Course extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // 'rescene', 'jeju-kdrama' 처럼 URL에 쓰는 식별자.
  @Column(nullable = false, length = 60)
  private String code;

  @Column(name = "title_ko", nullable = false, length = 120)
  private String titleKo;

  @Column(name = "title_en", nullable = false, length = 200)
  private String titleEn;

  // 원작 이름. '폭싹 속았수다 (2025)' 처럼 연도까지 붙은 표기를 그대로 둔다.
  @Column(length = 160)
  private String work;

  // 코스가 도는 지역. 여러 지역에 걸치면 대표 지역만 둔다.
  @Column(name = "region_ko", length = 40)
  private String regionKo;

  @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy("sequence asc")
  private final List<CoursePlace> places = new ArrayList<>();

  public Course(String code, String titleKo, String titleEn) {
    this.code = code;
    this.titleKo = titleKo;
    this.titleEn = titleEn;
  }

  public void updateWork(String work, String regionKo) {
    this.work = work;
    this.regionKo = regionKo;
  }

  public void addPlace(CoursePlace coursePlace) {
    places.add(coursePlace);
  }
}
