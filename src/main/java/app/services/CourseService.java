package app.services;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import app.domain.Course;
import app.domain.User;
import app.domain.dto.CourseDTO;
import app.domain.enums.RoleType;
import app.exceptions.ResourceNotFoundException;
import app.repositories.CourseRepository;
import app.security.AuthenticatedUser;

@Service
public class CourseService {

	@Autowired
	private CourseRepository courseRepository;
	
	@Autowired
	private AuthenticatedUser authenticatedUser;

	public List<Course> findAll() {
		return courseRepository.findAll();
	}

	public Course findById(Long id) {
		return courseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException());
	}

	public Course create(CourseDTO obj) {
		//Apenas professores e admins podem criar cursos
		User user = authenticatedUser.getAuthenticatedUser();
		
		if (user.getRole().equals(RoleType.STUDENT))
			throw new AccessDeniedException("You have no permission to access this endpoint.");
		
		Course course = new Course(null, obj.title(), obj.description(), user, ZonedDateTime.now().toLocalDate());
		
		return courseRepository.save(course);
	}

	public Course update(CourseDTO obj, Long id) {
		//Verificar se quem está atualizando é o professor ou admin
		User user = authenticatedUser.getAuthenticatedUser();
		
		if (user.getRole().equals(RoleType.STUDENT))
			throw new AccessDeniedException("You have no permission to access this endpoint.");
		
		Course existingCourse = this.findById(id);
		
		if (user.getRole().equals(RoleType.TEACHER) && !user.getId().equals(existingCourse.getTeacher().getId()))
			throw new AccessDeniedException("You can't update other teacher courses");
		
		existingCourse.setTitle(obj.title());
		existingCourse.setDescription(obj.description());
		
		return courseRepository.save(existingCourse);
	}

	public void delete(Long id) {
		//Verificar se quem está deletando é o professor ou admin
		User user = authenticatedUser.getAuthenticatedUser();
		
		if (user.getRole().equals(RoleType.STUDENT))
			throw new AccessDeniedException("You have no permission to access this endpoint.");
		
		Course existingCourse = this.findById(id);
		
		if (user.getRole().equals(RoleType.TEACHER) && !user.getId().equals(existingCourse.getTeacher().getId()))
			throw new AccessDeniedException("You can't delete other teacher courses");
		
		courseRepository.deleteById(id);
	}
	
	
}
