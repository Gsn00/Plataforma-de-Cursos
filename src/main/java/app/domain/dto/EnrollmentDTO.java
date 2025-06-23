package app.domain.dto;

import jakarta.validation.constraints.NotNull;

public record EnrollmentDTO(@NotNull Long userId, @NotNull Long courseId) {

}
