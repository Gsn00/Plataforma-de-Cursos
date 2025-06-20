package app.domain.dto;

import app.domain.enums.RoleType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterDTO(@NotBlank String name, @NotBlank @Email String email, @NotBlank String password, @NotNull RoleType role) {

}
