package app.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LessonDTO(@NotBlank String title, @NotBlank String description, @NotNull Long courseId, @NotNull Integer sequence, @NotBlank String videoUrl) {

}
