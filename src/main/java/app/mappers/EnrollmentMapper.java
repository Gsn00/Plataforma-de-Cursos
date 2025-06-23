package app.mappers;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import app.domain.Course;
import app.domain.Enrollment;
import app.domain.dto.CourseResponse;
import app.domain.dto.EnrollmentResponse;
import app.domain.dto.UserResponse;

@Component
public class EnrollmentMapper {

	public EnrollmentResponse toDTO(Enrollment enrollment) {
		UserResponse userResponse = new UserResponse
				(enrollment.getUser().getId(), enrollment.getUser().getName(), enrollment.getUser().getEmail());
		
		Course course = enrollment.getCourse();
		UserResponse teacherResponse = new UserResponse
				(course.getTeacher().getId(), course.getTeacher().getName(), course.getTeacher().getEmail());
		CourseResponse courseResponse = new CourseResponse
				(course.getId(), course.getTitle(), course.getDescription(), teacherResponse, course.getCreationDate());
		
		LocalDate today = ZonedDateTime.now().toLocalDate();
		
		EnrollmentResponse enrollmentResponse = new EnrollmentResponse
				(enrollment.getId(), userResponse, courseResponse, today);
		
		return enrollmentResponse;
	}
	
	public List<EnrollmentResponse> toDTO(List<Enrollment> list) {
		List<EnrollmentResponse> enrollmentsResponse = list.stream().map(enrollment -> {
			return this.toDTO(enrollment);
		}).collect(Collectors.toList());
		
		return enrollmentsResponse;
	}
}
