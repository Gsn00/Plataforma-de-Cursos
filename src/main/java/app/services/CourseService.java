package app.services;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import app.domain.Course;
import app.domain.User;
import app.domain.dto.CourseDTO;
import app.domain.dto.CourseResponse;
import app.domain.enums.RoleType;
import app.exceptions.ResourceNotFoundException;
import app.mappers.CourseMapper;
import app.repositories.CourseRepository;
import app.security.AuthenticatedUser;

@Service
public class CourseService {

	@Autowired
	private CourseRepository courseRepository;
	
	@Autowired
	private AuthenticatedUser authenticatedUser;
	
	@Autowired
	private CourseMapper courseMapper;

	public List<CourseResponse> findAll() {
		List<Course> courses = courseRepository.findAll();
		return courseMapper.toDTO(courses);
	}

	public Course findByIdEntity(Long id) {
		Course course = courseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Course not found."));		
		return course;
	}
	
	public CourseResponse findById(Long id) {
		Course course = this.findByIdEntity(id);		
		return courseMapper.toDTO(course);
	}

	public CourseResponse create(CourseDTO obj) {
		//Apenas professores e admins podem criar cursos
		User user = authenticatedUser.getAuthenticatedUser();
		
		if (user.getRole().equals(RoleType.STUDENT))
			throw new AccessDeniedException("You have no permission to access this endpoint.");
		
		Course course = new Course(null, obj.title(), obj.description(), user, ZonedDateTime.now().toLocalDate());
		course = courseRepository.save(course);
		
		return courseMapper.toDTO(course);
	}

	public CourseResponse update(CourseDTO obj, Long id) {
		//Verificar se quem está atualizando é o professor ou admin
		User user = authenticatedUser.getAuthenticatedUser();
		Course existingCourse = this.findByIdEntity(id);
		
		if (user.getRole().equals(RoleType.STUDENT))
			throw new AccessDeniedException("You have no permission to access this endpoint.");
		if (user.getRole().equals(RoleType.TEACHER) && !user.getId().equals(existingCourse.getTeacher().getId()))
			throw new AccessDeniedException("You can't update other teacher courses");
		
		existingCourse.setTitle(obj.title());
		existingCourse.setDescription(obj.description());
		
		existingCourse = courseRepository.save(existingCourse);
		
		return courseMapper.toDTO(existingCourse);
	}

	public void delete(Long id) {
		//Verificar se quem está deletando é o professor ou admin
		User user = authenticatedUser.getAuthenticatedUser();
		Course existingCourse = this.findByIdEntity(id);
		
		if (user.getRole().equals(RoleType.STUDENT))
			throw new AccessDeniedException("You have no permission to access this endpoint.");
		if (user.getRole().equals(RoleType.TEACHER) && !user.getId().equals(existingCourse.getTeacher().getId()))
			throw new AccessDeniedException("You can't delete other teacher courses");
		
		courseRepository.deleteById(id);
	}
	
	
}
