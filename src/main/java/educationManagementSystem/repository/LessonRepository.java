package educationManagementSystem.repository;

import educationManagementSystem.model.education.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Integer> {
    Optional<Lesson> findById(Integer lessonId);
    Optional<Lesson> findByName(String name);
}
