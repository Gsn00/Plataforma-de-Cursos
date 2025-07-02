package app.config;

import java.time.LocalDate;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import app.domain.Course;
import app.domain.Enrollment;
import app.domain.Lesson;
import app.domain.User;
import app.domain.enums.RoleType;
import app.repositories.CourseRepository;
import app.repositories.EnrollmentRepository;
import app.repositories.LessonRepository;
import app.repositories.UserRepository;

@Configuration
public class TestConfig implements CommandLineRunner {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CourseRepository courseRepository;
	
	@Autowired
	private LessonRepository lessonRepository;
	
	@Autowired
	private EnrollmentRepository enrollmentRepository;
	
	@Override
	public void run(String... args) throws Exception {
		User u1 = new User(null, "John Doe", "john@gmail.com", new BCryptPasswordEncoder().encode("john123"), RoleType.STUDENT);
		User u2 = new User(null, "Daryl Gray", "daryl@gmail.com", new BCryptPasswordEncoder().encode("arrows"), RoleType.TEACHER);
		User u3 = new User(null, "Ana Brown", "ana@gmail.com", new BCryptPasswordEncoder().encode("ana123"), RoleType.ADMIN);
		
		userRepository.saveAll(Arrays.asList(u1, u2, u3));
		
		LocalDate today = LocalDate.now();
		
		Course c1 = new Course(null, "Curso de Java", "Curso completo de Java e programação orientada a objetos.", u2, today);
		
		courseRepository.save(c1);
		
		Lesson l1 = new Lesson(null, "Aula 1 - Introdução ao Java", "Explicando o que é o Java", c1, 1, "");
		Lesson l2 = new Lesson(null, "Aula 2 - Instalando IDE", "Vamos instalar o Eclipse IDE", c1, 2, "");
		Lesson l3 = new Lesson(null, "Aula 3 - O que são variáveis", "Explicando o que são variáveis", c1, 3, "");
		
		lessonRepository.saveAll(Arrays.asList(l1, l2, l3));
		
		Enrollment e1 = new Enrollment(null, u1, c1, today);
		Enrollment e2 = new Enrollment(null, u3, c1, today.plusDays(1));
		
		enrollmentRepository.saveAll(Arrays.asList(e1, e2));
	}
}
