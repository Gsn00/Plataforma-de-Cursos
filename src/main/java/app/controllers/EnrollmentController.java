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

import app.domain.Enrollment;
import app.services.EnrollmentService;

@RestController
@RequestMapping("/enrollments")
public class EnrollmentController {

	@Autowired
	private EnrollmentService enrollmentService;
	
	@GetMapping
	public ResponseEntity<List<Enrollment>> findAll() {
		List<Enrollment> list = enrollmentService.findAll();
		return ResponseEntity.ok(list);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Enrollment> findById(@PathVariable Long id) {
		Enrollment enrollment = enrollmentService.findById(id);
		return ResponseEntity.ok(enrollment);
	}
	
	@PostMapping
	public ResponseEntity<Enrollment> create(@RequestBody Enrollment obj) {
		obj = enrollmentService.create(obj);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getId()).toUri();
		return ResponseEntity.created(uri).body(obj);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		enrollmentService.delete(id);
		return ResponseEntity.ok().build();
	}
}
