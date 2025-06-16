package app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import app.domain.Enrollment;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

}
