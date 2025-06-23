package app.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import app.domain.dto.EnrollmentDTO;
import app.domain.dto.EnrollmentResponse;
import app.services.EnrollmentService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/enrollments")
public class EnrollmentController {

	@Autowired
	private EnrollmentService enrollmentService;
	
	@GetMapping("/course/{id}")
	public ResponseEntity<List<EnrollmentResponse>> findAllByCourseId(@PathVariable Long id) {
		List<EnrollmentResponse> list = enrollmentService.findAllByCourseId(id);
		return ResponseEntity.ok(list);
	}
	
	@GetMapping("/user/{id}")
	public ResponseEntity<List<EnrollmentResponse>> findAllByUserId(@PathVariable Long id) {
		List<EnrollmentResponse> list = enrollmentService.findAllByUserId(id);
		return ResponseEntity.ok(list);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<EnrollmentResponse> findById(@PathVariable Long id) {
		EnrollmentResponse enrollment = enrollmentService.findById(id);
		return ResponseEntity.ok(enrollment);
	}
	
	@PostMapping
	public ResponseEntity<EnrollmentResponse> create(@RequestBody @Valid EnrollmentDTO obj) {
		EnrollmentResponse enrollment = enrollmentService.create(obj);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(enrollment.id()).toUri();
		return ResponseEntity.created(uri).body(enrollment);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		enrollmentService.delete(id);
		return ResponseEntity.ok().build();
	}
}
