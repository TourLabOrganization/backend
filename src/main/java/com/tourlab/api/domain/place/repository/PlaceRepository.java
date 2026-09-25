package com.tourlab.api.domain.place.repository;

import com.tourlab.api.domain.place.entity.Place;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceRepository extends JpaRepository<Place, Long> {

  Optional<Place> findByCode(String code);

  List<Place> findAllByRegionKo(String regionKo);

  boolean existsByCode(String code);
}
