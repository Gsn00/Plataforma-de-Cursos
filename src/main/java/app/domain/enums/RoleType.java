package app.domain.enums;

public enum RoleType {
	
	STUDENT("student"),
	TEACHER("teacher"),
	ADMIN("admin");
	
	private String role;
	
	RoleType(String role) {
		this.role = role;
	}
	
	String getRole() {
		return role.toString();
	}
}
