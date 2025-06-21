package app.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import app.domain.Lesson;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
	@Modifying
	@Transactional
	void deleteAllByCourseTeacherId(Long courseId);
	
	@Modifying
	@Transactional
	void deleteAllByCourseId(Long courseId);
	
	List<Lesson> findAllByCourseId(Long courseId);
}
