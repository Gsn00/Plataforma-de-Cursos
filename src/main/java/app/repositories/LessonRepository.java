package app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import app.domain.Lesson;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
	@Modifying
	@Transactional
	void deleteAllByCourseTeacherId(Long userId);
	
	@Modifying
	@Transactional
	void deleteAllByCourseId(Long userId);
}
