package educationManagementSystem.repository;

import java.util.Optional;

import educationManagementSystem.model.ERole;
import educationManagementSystem.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}
