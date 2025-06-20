package app.mappers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import app.domain.Course;
import app.domain.dto.CourseResponse;
import app.domain.dto.UserResponse;

@Component
public class CourseMapper {
	
	public CourseResponse toDTO(Course course) {
		UserResponse userResponse = new UserResponse
				(course.getTeacher().getId(), course.getTeacher().getName(), course.getTeacher().getEmail());
		
		CourseResponse courseResponse = new CourseResponse
				(course.getId(), course.getTitle(), course.getDescription(), userResponse, course.getCreationDate());
		
		return courseResponse;
	}
	
	public List<CourseResponse> toDTO(List<Course> list) {
			List<CourseResponse> coursesResponse = list.stream().map(course -> {
			
			UserResponse userResponse = new UserResponse
					(course.getTeacher().getId(), course.getTeacher().getName(), course.getTeacher().getEmail());
			
			CourseResponse courseResponse = new CourseResponse
					(course.getId(), course.getTitle(), course.getDescription(), userResponse, course.getCreationDate());
			return courseResponse;
			
		}).collect(Collectors.toList());
		
		return coursesResponse;
	}
}
