package app.services;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import app.domain.Course;
import app.domain.Lesson;
import app.domain.User;
import app.domain.dto.CourseResponse;
import app.domain.dto.LessonDTO;
import app.domain.dto.LessonResponse;
import app.domain.enums.RoleType;
import app.exceptions.ResourceNotFoundException;
import app.mappers.CourseMapper;
import app.mappers.LessonMapper;
import app.repositories.LessonRepository;
import app.security.AuthenticatedUser;
import app.streaming.GumletClient;

@Service
public class LessonService {

	@Autowired
	private LessonRepository lessonRepository;
	
	@Autowired
	private LessonMapper lessonMapper;
	
	@Autowired
	private AuthenticatedUser authenticatedUser;
	
	@Autowired
	private EnrollmentService enrollmentService;
	
	@Autowired
	private CourseService courseService;
	
	@Autowired
	private GumletClient gumletClient;
	
	@Autowired
	private CourseMapper courseMapper;

	public List<LessonResponse> findAllByCourseId(Long id) {
		//Apenas o próprio professor, alunos matriculados e admin poderão ter acesso.
		User user = authenticatedUser.getAuthenticatedUser();
		CourseResponse course = courseService.findById(id);
		
		boolean isUserEnrolled = enrollmentService.isUserEnrolled(user.getId(), id);
		boolean isUserTheTeacher = (course.teacher().id().equals(user.getId()));
		
		if (!user.getRole().equals(RoleType.ADMIN) && !isUserEnrolled && !isUserTheTeacher)
			throw new AccessDeniedException("You are not enrolled in this course");
		
		List<Lesson> lessons = lessonRepository.findAllByCourseId(id);
		return lessonMapper.toDTO(lessons);
	}

	public Lesson findByIdEntity(Long id) {
		Lesson lesson = lessonRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Lesson not found."));
		return lesson;
	}
	
	public LessonResponse findById(Long id) {
		//Apenas o próprio professor, alunos matriculados e admin poderão ter acesso.
		User user = authenticatedUser.getAuthenticatedUser();
		Lesson lesson = this.findByIdEntity(id);
		Course course = lesson.getCourse();
			
		boolean isUserEnrolled = enrollmentService.isUserEnrolled(user.getId(), course.getId());
		boolean isUserTheTeacher = (course.getTeacher().getId().equals(user.getId()));
		
		if (!user.getRole().equals(RoleType.ADMIN) && !isUserEnrolled && !isUserTheTeacher)
			throw new AccessDeniedException("You are not enrolled in this course");
		
		return lessonMapper.toDTO(lesson);
	}

	public LessonResponse create(LessonDTO obj, MultipartFile videoFile) {
		//Apenas admins e o próprio professor podem criar uma nova aula.
		User user = authenticatedUser.getAuthenticatedUser();
		Course course = courseService.findByIdEntity(obj.courseId());
		CourseResponse courseResponse = courseMapper.toDTO(course);
		
		boolean isUserTheTeacher = (course.getTeacher().getId().equals(user.getId()));
		
		if (!user.getRole().equals(RoleType.ADMIN) && !isUserTheTeacher)
			throw new AccessDeniedException("You can't create lessons for this course.");
		
		Map<String, String> assets = gumletClient.requestUploadInfo(obj.title(), obj.description());
		gumletClient.uploadVideoToGumlet(assets.get("asset_id"), videoFile);
		
		Lesson lesson = new Lesson(null, obj.title(), obj.description(), course, obj.sequence(), assets.get("playback_url"));	
		lesson = lessonRepository.save(lesson);
		
		return new LessonResponse
				(lesson.getId(), lesson.getTitle(), lesson.getDescription(), courseResponse, lesson.getSequence(), lesson.getVideoUrl());
	}

	public LessonResponse update(LessonDTO obj, Long id) {
		//Apenas admins e o próprio professor podem atualizar uma aula.
		User user = authenticatedUser.getAuthenticatedUser();
		Course course = courseService.findByIdEntity(obj.courseId());
		CourseResponse courseResponse = courseService.findById(obj.courseId());
		Lesson existingLesson = lessonRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Lesson not found."));
		
		boolean isUserTheTeacher = (course.getTeacher().getId().equals(user.getId()));
		
		if (!user.getRole().equals(RoleType.ADMIN) && !isUserTheTeacher)
			throw new AccessDeniedException("You can't create lessons for this course.");
		
		existingLesson.setTitle(obj.title());
		existingLesson.setDescription(obj.description());
		existingLesson.setSequence(obj.sequence());
		
		Lesson lesson = lessonRepository.save(existingLesson);
		
		return new LessonResponse
				(lesson.getId(), lesson.getTitle(), lesson.getDescription(), courseResponse, lesson.getSequence(), lesson.getVideoUrl());
	}

	public void delete(Long id) {
		//Verificar se quem está deletando é o professor ou admin
		User user = authenticatedUser.getAuthenticatedUser();
		Lesson lesson = this.findByIdEntity(id);
		
		if (user.getRole().equals(RoleType.STUDENT))
			throw new AccessDeniedException("You have no permission to access this endpoint.");
		if (user.getRole().equals(RoleType.TEACHER) && !user.getId().equals(lesson.getCourse().getTeacher().getId()))
			throw new AccessDeniedException("You can't delete other teacher lesson");
		
		lessonRepository.deleteById(id);
	}
}
