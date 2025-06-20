package app.domain.dto;

import java.time.LocalDate;

public record CourseResponse(Long id, String title, String description, UserResponse teacher, LocalDate creationDate) {

}
