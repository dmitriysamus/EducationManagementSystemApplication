package educationManagementSystem.repository;

import educationManagementSystem.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Integer> {
    Optional<Token> findById(Integer token_id);
    Token findByToken(String token);

    List<Token> findByExpiryDateBefore(LocalDateTime localDateTime);
    List<Token> findByActiveFalse();
    Boolean existsByToken(String token);
}
