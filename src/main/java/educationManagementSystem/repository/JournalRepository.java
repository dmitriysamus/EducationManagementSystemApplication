package educationManagementSystem.repository;

import educationManagementSystem.model.education.Group;
import educationManagementSystem.model.education.Journal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JournalRepository extends JpaRepository<Journal, Integer> {
    Optional<Journal> findById(Integer id);
}
