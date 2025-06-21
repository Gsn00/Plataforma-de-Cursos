package app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import app.domain.Enrollment;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
	@Modifying
	@Transactional
	void deleteAllByUserId(Long userId);
	
	@Modifying
	@Transactional
	void deleteAllByCourseId(Long courseId);
	
	@Modifying
	@Transactional
	void deleteAllByCourseTeacherId(Long userId);
	
	boolean existsByUserIdAndCourseId(Long userId, Long courseId);
}
