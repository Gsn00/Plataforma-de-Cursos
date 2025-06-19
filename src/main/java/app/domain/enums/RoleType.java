package app.domain.enums;

public enum RoleType {
	
	STUDENT,
	TEACHER,
	ADMIN;
	
	public static RoleType fromString(String value) {
		return RoleType.valueOf(value.toUpperCase());
	}
}
