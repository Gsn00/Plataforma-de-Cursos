package app.repositories;

import java.util.List;

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
	
	List<Enrollment> findAllByCourseId(Long courseId);
	
	List<Enrollment> findAllByUserId(Long userId);
	
	boolean existsByUserIdAndCourseId(Long userId, Long courseId);
}
