package app.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import app.domain.dto.LessonDTO;
import app.domain.dto.LessonResponse;
import app.services.LessonService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/lessons")
public class LessonController {

	@Autowired
	private LessonService lessonService;
	
	@GetMapping("/course/{id}")
	public ResponseEntity<List<LessonResponse>> findAllByCourseId(@PathVariable Long id) {
		List<LessonResponse> list = lessonService.findAllByCourseId(id);
		return ResponseEntity.ok(list);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<LessonResponse> findById(@PathVariable Long id) {
		LessonResponse lesson = lessonService.findById(id);
		return ResponseEntity.ok(lesson);
	}
	
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<LessonResponse> create(@RequestPart("data") @Valid LessonDTO obj, @RequestPart("file") MultipartFile videoFile) {
		LessonResponse response = lessonService.create(obj, videoFile);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(response.id()).toUri();
		return ResponseEntity.created(uri).body(response);
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<LessonResponse> update(@RequestBody @Valid LessonDTO obj, @PathVariable Long id) {
		LessonResponse response  = lessonService.update(obj, id);
		return ResponseEntity.ok(response);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		lessonService.delete(id);
		return ResponseEntity.ok().build();
	}
}
