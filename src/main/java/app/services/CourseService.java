package app.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import app.domain.Course;
import app.exceptions.ResourceNotFoundException;
import app.repositories.CourseRepository;

@Service
public class CourseService {

	@Autowired
	private CourseRepository courseRepository;

	public List<Course> findAll() {
		return courseRepository.findAll();
	}

	public Course findById(Long id) {
		return courseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException());
	}

	public Course create(Course obj) {
		//Verificar se quem está criando um professor ou admin
		return courseRepository.save(obj);
	}

	public Course update(Course obj, Long id) {
		//Verificar se quem está atualizando é o professor ou admin
		Course existingCourse = this.findById(id);
		
		existingCourse.setTitle(obj.getTitle());
		existingCourse.setDescription(obj.getDescription());
		existingCourse.setTeacher(obj.getTeacher());
		
		return courseRepository.save(existingCourse);
	}

	public void delete(Long id) {
		//Verificar se quem está deletando é o professor ou admin
		courseRepository.deleteById(id);
	}
	
	
}
