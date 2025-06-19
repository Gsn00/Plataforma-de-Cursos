package app.domain.lifecycle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import app.repositories.CourseRepository;
import app.repositories.EnrollmentRepository;
import app.repositories.LessonRepository;
import app.repositories.RefreshTokenRepository;
import app.repositories.UserRepository;

@Transactional
@Component
public class CascadeDeletionManager {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CourseRepository courseRepository;
	
	@Autowired
	private LessonRepository lessonRepository;
	
	@Autowired
	private EnrollmentRepository enrollmentRepository;
	
	@Autowired
	private RefreshTokenRepository refreshTokenRepository;
	
	public void deleteAllEnrollmentsByUserId(Long id) {
		enrollmentRepository.deleteAllByUserId(id);
	}
	
	public void deleteAllEnrollmentsByCourseId(Long id) {
		enrollmentRepository.deleteAllByCourseId(id);
	}
	
	public void deleteAllEnrollmentsByCourseTeacherId(Long id) {
		enrollmentRepository.deleteAllByCourseTeacherId(id);
	}
	
	public void deleteAllLessonsByCourseTeacherId(Long id) {
		lessonRepository.deleteAllByCourseTeacherId(id);
	}
	
	public void deleteAllLessonsByCourseId(Long id) {
		lessonRepository.deleteAllByCourseId(id);
	}
	
	public void deleteAllCoursesByTeacherId(Long id) {
		this.deleteAllEnrollmentsByCourseTeacherId(id);
		this.deleteAllLessonsByCourseTeacherId(id);
		courseRepository.deleteAllByTeacherId(id);
	}
	
	public void deleteCourseById(Long id) {
		this.deleteAllEnrollmentsByCourseId(id);
		this.deleteAllLessonsByCourseId(id);
		courseRepository.deleteById(id);
	}
	
	public void deleteStudentAndDependenciesByUserId(Long id) {
		this.deleteAllEnrollmentsByUserId(id);
		refreshTokenRepository.deleteByUserId(id);
		userRepository.deleteById(id);
	}
	
	public void deleteTeacherAndDependenciesByUserId(Long id) {
		this.deleteAllEnrollmentsByUserId(id);
		this.deleteAllCoursesByTeacherId(id);
		refreshTokenRepository.deleteByUserId(id);
		userRepository.deleteById(id);
	}
}
