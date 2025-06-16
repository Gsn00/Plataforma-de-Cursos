package app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import app.domain.Course;

public interface CourseRepository extends JpaRepository<Course, Long> {

}
