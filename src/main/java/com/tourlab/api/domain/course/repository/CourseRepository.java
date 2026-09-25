package com.tourlab.api.domain.course.repository;

import com.tourlab.api.domain.course.entity.Course;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {

  Optional<Course> findByCode(String code);
}
