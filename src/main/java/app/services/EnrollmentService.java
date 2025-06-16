package app.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import app.domain.Enrollment;
import app.exceptions.ResourceNotFoundException;
import app.repositories.EnrollmentRepository;

@Service
public class EnrollmentService {

	@Autowired
	private EnrollmentRepository enrollmentRepository;

	public List<Enrollment> findAll() {
		return enrollmentRepository.findAll();
	}

	public Enrollment findById(Long id) {
		return enrollmentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException());
	}

	public Enrollment create(Enrollment obj) {
		//Verificar se quem está criando é um admin
		return enrollmentRepository.save(obj);
	}

	public void delete(Long id) {
		//Verificar se quem está deletando é um admin ou o próprio aluno
		enrollmentRepository.deleteById(id);
	}
	
	
}
