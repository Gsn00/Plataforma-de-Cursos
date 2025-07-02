package app.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.ZonedDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import app.domain.Course;
import app.domain.User;
import app.domain.dto.CourseDTO;
import app.domain.dto.CourseResponse;
import app.domain.dto.UserResponse;
import app.domain.enums.RoleType;
import app.mappers.CourseMapper;
import app.repositories.CourseRepository;
import app.security.AuthenticatedUser;

@ExtendWith(MockitoExtension.class)
public class CourseServiceTest {
	
	@InjectMocks
	private CourseService courseService;
	
	@Mock
	private CourseRepository courseRepository;
	
	@Mock
	private AuthenticatedUser authenticatedUser;
	
	@Mock
	private CourseMapper courseMapper;
	
	@Test
	void shouldAllowTeacherToCreateNewCourses() {
		User authUser = new User(1L, "Gabriel", "gabriel@gmail.com", "encodedPasssword", RoleType.TEACHER);
		CourseDTO dto = new CourseDTO("Course Title", "Course Desc");
		Course course = new Course(1L, dto.title(), dto.description(), authUser, ZonedDateTime.now().toLocalDate());
		UserResponse userResponse = new UserResponse
				(authUser.getId(), authUser.getName(), authUser.getEmail());
		CourseResponse courseResponse = new CourseResponse
				(course.getId(), course.getTitle(), course.getDescription(), userResponse, course.getCreationDate());
		
		when(authenticatedUser.getAuthenticatedUser()).thenReturn(authUser);
		when(courseRepository.save(any(Course.class))).thenReturn(course);
		when(courseMapper.toDTO(course)).thenReturn(courseResponse);
		
		CourseResponse response = courseService.create(dto);
		
		verify(courseRepository).save(any(Course.class));
		verify(courseMapper).toDTO(course);
		assertEquals("Course Title", response.title());
		assertEquals("Course Desc", response.description());
	}
	
	@Test
	void shouldThrowExceptionIfStudentTriesToCreateCourses() {
		User authUser = new User(1L, "Gabriel", "gabriel@gmail.com", "encodedPasssword", RoleType.STUDENT);
		CourseDTO dto = new CourseDTO("Course Title", "Course Desc");
		
		when(authenticatedUser.getAuthenticatedUser()).thenReturn(authUser);
		
		assertThrows(AccessDeniedException.class, () -> courseService.create(dto));
		
		verifyNoInteractions(courseRepository);
		verifyNoInteractions(courseMapper);
	}
	
	@Test
	void shouldPerformUpdateIfUserIsTheTeacher() {
		User authUser = new User(1L, "Gabriel", "gabriel@gmail.com", "encodedPasssword", RoleType.TEACHER);
		
		CourseDTO dto = new CourseDTO("New Title", "New Desc");
		Course existingCourse = new Course(1L, "Old Title", "Old Desc", authUser, ZonedDateTime.now().toLocalDate());
		UserResponse teacherResponse = new UserResponse
				(authUser.getId(), authUser.getName(), authUser.getEmail());
		CourseResponse courseResponse = new CourseResponse
				(1L, dto.title(), dto.description(), teacherResponse, ZonedDateTime.now().toLocalDate());
		
		when(authenticatedUser.getAuthenticatedUser()).thenReturn(authUser);
		when(courseRepository.findById(1L)).thenReturn(Optional.of(existingCourse));
		when(courseRepository.save(existingCourse)).thenReturn(existingCourse);
		when(courseMapper.toDTO(any(Course.class))).thenReturn(courseResponse);
		
		CourseResponse response = courseService.update(dto, 1L);
		
		verify(courseRepository).save(any(Course.class));
		verify(courseMapper).toDTO(any(Course.class));
		assertEquals("New Title", response.title());
		assertEquals("New Desc", response.description());
	}
	
	@Test
	void shouldThrowExceptionIfTeacherTriesToUpdateOtherTeachersCourse() {
		User authUser = new User(1L, "Gabriel", "gabriel@gmail.com", "encodedPasssword", RoleType.TEACHER);
		User teacher = new User(2L, "Teacher", "teacher@gmail.com", "encodedPasssword", RoleType.TEACHER);
		
		CourseDTO dto = new CourseDTO("New Title", "New Desc");
		Course existingCourse = new Course(1L, "Old Title", "Old Desc", teacher, ZonedDateTime.now().toLocalDate());
		
		when(authenticatedUser.getAuthenticatedUser()).thenReturn(authUser);
		when(courseRepository.findById(2L)).thenReturn(Optional.of(existingCourse));
		
		assertThrows(AccessDeniedException.class, () -> courseService.update(dto, 2L));
		verifyNoMoreInteractions(courseRepository);
		verifyNoInteractions(courseMapper);
	}
	
	@Test
	void shouldThrowExceptionIfStudentTriesToUpdateACourse() {
		User authUser = new User(1L, "Gabriel", "gabriel@gmail.com", "encodedPasssword", RoleType.STUDENT);
		User teacher = new User(2L, "Teacher", "teacher@gmail.com", "encodedPasssword", RoleType.TEACHER);
		
		CourseDTO dto = new CourseDTO("New Title", "New Desc");
		Course existingCourse = new Course(1L, "Old Title", "Old Desc", teacher, ZonedDateTime.now().toLocalDate());
		
		when(authenticatedUser.getAuthenticatedUser()).thenReturn(authUser);
		when(courseRepository.findById(2L)).thenReturn(Optional.of(existingCourse));
		
		assertThrows(AccessDeniedException.class, () -> courseService.update(dto, 2L));
		verifyNoMoreInteractions(courseRepository);
		verifyNoInteractions(courseMapper);
	}
	
	@Test
	void shouldPerformDeleteWithSuccess() {
		User authUser = new User(1L, "Gabriel", "gabriel@gmail.com", "encodedPasssword", RoleType.TEACHER);
		
		Course existingCourse = new Course(1L, "Title", "Desc", authUser, ZonedDateTime.now().toLocalDate());
		
		when(authenticatedUser.getAuthenticatedUser()).thenReturn(authUser);
		when(courseRepository.findById(1L)).thenReturn(Optional.of(existingCourse));
		
		courseService.delete(1L);
		
		verify(courseRepository).deleteById(1L);
		verifyNoMoreInteractions(courseRepository);
	}
	
	@Test
	void shouldThrowExceptionIfTeacherTriesToDeleteOtherTeacherCourse() {
		User authUser = new User(1L, "Gabriel", "gabriel@gmail.com", "encodedPasssword", RoleType.TEACHER);
		User teacher = new User(2L, "Teacher", "teacher@gmail.com", "encodedPasssword", RoleType.TEACHER);
		
		Course existingCourse = new Course(1L, "Title", "Desc", teacher, ZonedDateTime.now().toLocalDate());
		
		when(authenticatedUser.getAuthenticatedUser()).thenReturn(authUser);
		when(courseRepository.findById(1L)).thenReturn(Optional.of(existingCourse));
		
		assertThrows(AccessDeniedException.class, () -> courseService.delete(1L));
		verify(courseRepository).findById(1L);
		verify(courseRepository, never()).deleteById(any());
		verifyNoMoreInteractions(courseRepository);
	}
	
	@Test
	void shouldThrowExceptionIfStudentTriesToDeleteACourse() {
		User authUser = new User(1L, "Gabriel", "gabriel@gmail.com", "encodedPasssword", RoleType.STUDENT);
		User teacher = new User(2L, "Teacher", "teacher@gmail.com", "encodedPasssword", RoleType.TEACHER);
		
		Course existingCourse = new Course(1L, "Title", "Desc", teacher, ZonedDateTime.now().toLocalDate());
		
		when(authenticatedUser.getAuthenticatedUser()).thenReturn(authUser);
		when(courseRepository.findById(1L)).thenReturn(Optional.of(existingCourse));
		
		assertThrows(AccessDeniedException.class, () -> courseService.delete(1L));
		verify(courseRepository).findById(1L);
		verify(courseRepository, never()).deleteById(any());
		verifyNoMoreInteractions(courseRepository);
	}
}

