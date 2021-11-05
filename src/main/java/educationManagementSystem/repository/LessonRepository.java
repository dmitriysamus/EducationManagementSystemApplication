package educationManagementSystem.repository;

import educationManagementSystem.model.education.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LessonRepository extends JpaRepository<Lesson, Integer> {
    Optional<Lesson> findById(Integer lessonId);
}
