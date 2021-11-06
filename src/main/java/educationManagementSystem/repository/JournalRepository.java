package educationManagementSystem.repository;

import educationManagementSystem.model.education.Group;
import educationManagementSystem.model.education.Journal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JournalRepository extends JpaRepository<Journal, Integer> {
    Optional<Journal> findById(Integer id);
}
