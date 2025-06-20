package app.domain.dto;

import jakarta.validation.constraints.NotBlank;

public record CourseDTO(@NotBlank String title, @NotBlank String description) {

}
