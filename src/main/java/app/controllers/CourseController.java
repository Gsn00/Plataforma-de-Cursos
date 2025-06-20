package app.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import app.domain.dto.CourseDTO;
import app.domain.dto.CourseResponse;
import app.services.CourseService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/courses")
public class CourseController {

	@Autowired
	private CourseService courseService;
	
	@GetMapping
	public ResponseEntity<List<CourseResponse>> findAll() {
		List<CourseResponse> list = courseService.findAll();
		return ResponseEntity.ok(list);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<CourseResponse> findById(@PathVariable Long id) {
		CourseResponse course = courseService.findById(id);
		return ResponseEntity.ok(course);
	}
	
	@PostMapping
	public ResponseEntity<CourseResponse> create(@RequestBody @Valid CourseDTO obj) {
		CourseResponse course = courseService.create(obj);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(course.id()).toUri();
		return ResponseEntity.created(uri).body(course);
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<CourseResponse> update(@RequestBody @Valid CourseDTO obj, @PathVariable Long id) {
		CourseResponse course = courseService.update(obj, id);
		return ResponseEntity.ok(course);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		courseService.delete(id);
		return ResponseEntity.ok().build();
	}
}
