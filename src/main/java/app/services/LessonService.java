package app.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import app.domain.Lesson;
import app.exceptions.ResourceNotFoundException;
import app.repositories.LessonRepository;

@Service
public class LessonService {

	@Autowired
	private LessonRepository lessonRepository;

	public List<Lesson> findAll() {
		return lessonRepository.findAll();
	}

	public Lesson findById(Long id) {
		return lessonRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException());
	}

	public Lesson create(Lesson obj) {
		return lessonRepository.save(obj);
	}

	public Lesson update(Lesson obj, Long id) {
		Lesson existingLesson = this.findById(id);
		
		existingLesson.setTitle(obj.getTitle());
		existingLesson.setDescription(obj.getDescription());
		existingLesson.setSequence(obj.getSequence());
		existingLesson.setVideoUrl(obj.getVideoUrl());
		
		return lessonRepository.save(existingLesson);
	}

	public void delete(Long id) {
		lessonRepository.deleteById(id);
	}
	
	
}
