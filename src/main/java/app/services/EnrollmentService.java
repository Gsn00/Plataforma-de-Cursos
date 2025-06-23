package app.services;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import app.domain.Course;
import app.domain.Enrollment;
import app.domain.User;
import app.domain.dto.EnrollmentDTO;
import app.domain.dto.EnrollmentResponse;
import app.domain.enums.RoleType;
import app.exceptions.ResourceNotFoundException;
import app.exceptions.UserAlreadyEnrolledException;
import app.mappers.EnrollmentMapper;
import app.repositories.EnrollmentRepository;
import app.security.AuthenticatedUser;

@Service
public class EnrollmentService {

	@Autowired
	private EnrollmentRepository enrollmentRepository;
	
	@Autowired
	private EnrollmentMapper enrollmentMapper;
	
	@Autowired
	private CourseService courseService;
	
	@Autowired
	private AuthenticatedUser authenticatedUser;

	public List<EnrollmentResponse> findAllByCourseId(Long courseId) {
		//Apenas admins e o próprio professor pode acessar
		Course course = courseService.findByIdEntity(courseId);
		User user = authenticatedUser.getAuthenticatedUser();
		
		if (!user.getRole().equals(RoleType.ADMIN) && !user.getId().equals(course.getTeacher().getId()))
			throw new AccessDeniedException("You have no permission to access this endpoint.");
		
		List<Enrollment> enrollments = enrollmentRepository.findAllByCourseId(courseId);
		return enrollmentMapper.toDTO(enrollments);
	}
	
	public List<EnrollmentResponse> findAllByUserId(Long userId) {
		//Apenas admins e o próprio aluno pode acessar
		User user = authenticatedUser.getAuthenticatedUser();
		
		if (!user.getRole().equals(RoleType.ADMIN) && !user.getId().equals(userId))
			throw new AccessDeniedException("You have no permission to access this endpoint.");
		
		List<Enrollment> enrollments = enrollmentRepository.findAllByUserId(userId);
		return enrollmentMapper.toDTO(enrollments);
	}

	public EnrollmentResponse findById(Long id) {
		//Apenas admin pode acessar
		User user = authenticatedUser.getAuthenticatedUser();
		
		if (!user.getRole().equals(RoleType.ADMIN))
			throw new AccessDeniedException("You have no permission to access this endpoint.");
		
		Enrollment enrollment = enrollmentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Enrollment not found."));
		return enrollmentMapper.toDTO(enrollment);
	}

	public EnrollmentResponse create(EnrollmentDTO obj) {
		//Todos poderão matricular a si mesmos, admins poderão matricular outros
		//Verificar se já não está matriculado
		//Verificar se o professor está se matriculando no próprio curso
		User user = authenticatedUser.getAuthenticatedUser();
		Course course = courseService.findByIdEntity(obj.courseId());
		
		if (!user.getRole().equals(RoleType.ADMIN) && !user.getId().equals(obj.userId()))
			throw new AccessDeniedException("You have no permission to access this endpoint.");
		if (enrollmentRepository.existsByUserIdAndCourseId(user.getId(), course.getId()))
			throw new UserAlreadyEnrolledException("You are already enrolled in this course");
		if (user.getId().equals(course.getTeacher().getId()))
			throw new AccessDeniedException("You can't enroll in this course.");
		
		LocalDate today = ZonedDateTime.now().toLocalDate();
		Enrollment enrollment = new Enrollment(null, user, course, today);
		
		enrollment = enrollmentRepository.save(enrollment);
		return enrollmentMapper.toDTO(enrollment);
	}

	public void delete(Long id) {
		//Todos podem deletar a própria matrícula, admins poderão deletar qualquer uma
		User user = authenticatedUser.getAuthenticatedUser();
		Enrollment enrollment = enrollmentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Enrollment not found"));
		
		if (!user.getRole().equals(RoleType.ADMIN) && !user.getId().equals(enrollment.getUser().getId()))
			throw new AccessDeniedException("You have no permission to access this endpoint.");
		
		enrollmentRepository.deleteById(id);
	}
	
	public boolean isUserEnrolled(Long userId, Long courseId) {
		return enrollmentRepository.existsByUserIdAndCourseId(userId, courseId);
	}
}
