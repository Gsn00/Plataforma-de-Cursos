package app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import app.domain.Lesson;

public interface LessonRepository extends JpaRepository<Lesson, Long> {

}
