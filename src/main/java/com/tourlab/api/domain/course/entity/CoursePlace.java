package com.tourlab.api.domain.course.entity;

import com.tourlab.api.domain.place.entity.Place;
import com.tourlab.api.global.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 코스에 장소가 어떻게 들어가는지. 같은 장소라도 코스마다 순번과 장면 설명이 달라서 장소 쪽이 아니라 여기에 둔다.
// 프로토타입의 off:true는 순번 없이 지도에만 찍히는 장소였다. 여기서는 sequence가 null인 것으로 나타낸다.
@Getter
@Entity
@Table(
    name = "course_places",
    uniqueConstraints =
        @UniqueConstraint(
            name = "ux_course_places_course_place",
            columnNames = {"course_id", "place_id"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CoursePlace extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "course_id", nullable = false)
  private Course course;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "place_id", nullable = false)
  private Place place;

  // 코스 안에서의 순번. null이면 번호 없이 지도에만 표시한다(주변 추천).
  private Integer sequence;

  // 이 장소가 나오는 영상 식별자.
  @Column(name = "video_key", length = 40)
  private String videoKey;

  // 영상에서 이 장소가 어떤 장면인지. '애순이네 동네' 같은 한 줄.
  @Column(name = "scene_ko", length = 200)
  private String sceneKo;

  @Column(name = "scene_en", length = 300)
  private String sceneEn;

  public CoursePlace(Course course, Place place, Integer sequence) {
    this.course = course;
    this.place = place;
    this.sequence = sequence;
  }

  public void updateScene(String videoKey, String sceneKo, String sceneEn) {
    this.videoKey = videoKey;
    this.sceneKo = sceneKo;
    this.sceneEn = sceneEn;
  }

  // 번호가 붙는 코스 구성 장소인지.
  public boolean isOnRoute() {
    return sequence != null;
  }
}
