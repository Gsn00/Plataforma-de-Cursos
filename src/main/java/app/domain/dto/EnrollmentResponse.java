package app.domain.dto;

import java.time.LocalDate;

public record EnrollmentResponse(Long id, UserResponse user, CourseResponse course, LocalDate enrollmentDate) {

}
