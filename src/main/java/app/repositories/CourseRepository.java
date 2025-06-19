package app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import app.domain.Course;

public interface CourseRepository extends JpaRepository<Course, Long> {
	@Modifying
	@Transactional
	void deleteAllByTeacherId(Long userId);
}
