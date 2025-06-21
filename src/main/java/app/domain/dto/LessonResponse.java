package app.domain.dto;

public record LessonResponse(Long id, String title, String description, CourseResponse course, Integer sequence, String videoUrl) {

}
