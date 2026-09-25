package com.tourlab.api.domain.place.entity;

// 장소 대분류. 프로토타입의 cat 값을 그대로 옮긴 것이라 아직 신뢰할 수 없다.
// 이름에 해수욕장·해변·해안이 들어간 91곳 중 41곳만 SEA이고, 해운대해수욕장·협재해수욕장은
// NATURE로 들어가 있다. TourAPI 표준 분류로 다시 매기기 전까지 필터 기준으로 쓰지 않는다.
public enum PlaceCategory {
  // 역사·문화유산
  HERITAGE,
  // 자연·힐링
  NATURE,
  // 체험·액티비티
  ACTIVITY,
  // 미식
  FOOD,
  // 해변·해안
  SEA,
  // 숙박
  STAY
}
