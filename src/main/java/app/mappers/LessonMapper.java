package app.mappers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import app.domain.Course;
import app.domain.Lesson;
import app.domain.dto.CourseResponse;
import app.domain.dto.LessonResponse;
import app.domain.dto.UserResponse;

@Component
public class LessonMapper {
	
	public LessonResponse toDTO(Lesson lesson) {
		Course course = lesson.getCourse();
		
		UserResponse userResponse = new UserResponse
				(course.getTeacher().getId(), course.getTeacher().getName(), course.getTeacher().getEmail());
		
		CourseResponse courseResponse = new CourseResponse
				(course.getId(), course.getTitle(), course.getDescription(), userResponse, course.getCreationDate());
		
		LessonResponse lessonResponse = new LessonResponse
				(lesson.getId(), lesson.getTitle(), lesson.getDescription(), courseResponse, lesson.getSequence(), lesson.getVideoUrl());
		
		return lessonResponse;
	}
	
	public List<LessonResponse> toDTO(List<Lesson> list) {
		List<LessonResponse> lessonsResponse = list.stream().map(lesson -> {
			Course course = lesson.getCourse();
				
			UserResponse userResponse = new UserResponse
					(course.getTeacher().getId(), course.getTeacher().getName(), course.getTeacher().getEmail());
			
			CourseResponse courseResponse = new CourseResponse
					(course.getId(), course.getTitle(), course.getDescription(), userResponse, course.getCreationDate());
			
			LessonResponse lessonResponse = new LessonResponse
					(lesson.getId(), lesson.getTitle(), lesson.getDescription(), courseResponse, lesson.getSequence(), lesson.getVideoUrl());
			
			return lessonResponse;
			
		}).collect(Collectors.toList());
		
		return lessonsResponse;
	}
}
