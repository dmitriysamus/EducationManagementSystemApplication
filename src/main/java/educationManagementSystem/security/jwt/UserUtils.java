package educationManagementSystem.security.jwt;

import educationManagementSystem.model.user.User;
import educationManagementSystem.payload.responce.MessageResponse;
import educationManagementSystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserUtils {
    @Value("${habatoo.app.jwtSecret}")
    private String jwtSecret;

    @Value("${habatoo.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    /**
     * Проверяет username и email на уникальность и отсуствие аналогов в существующей базе
     * @param user - данные пользователя для изменений
     * @param userFromDb - данные пользователя с дб
     */
    public ResponseEntity<?>  checkUserNameAndEmail(User user, User userFromDb) {
        if (!(user.getUsername().equals(userFromDb.getUsername())) & (userRepository.existsByUsername(user.getUsername()))) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (!(user.getEmail().equals(userFromDb.getEmail())) & (userRepository.existsByEmail(user.getEmail()))) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        userFromDb.setUsername(user.getUsername());
        userFromDb.setEmail(user.getEmail());
        userFromDb.setPassword(encoder.encode(user.getPassword()));

        userRepository.save(userFromDb);
        return ResponseEntity.ok(new MessageResponse("User data was update successfully!"));

    }


    //TODO сделать универсальный метод проверки уникальности
    /**
     * Проверяет username и email на уникальность и отсуствие аналогов в существующей базе
     * @param user - данные пользователя для изменений
     */
    public ResponseEntity<?>  checkRegisterUserNameAndEmail(User user) {

        if (userRepository.existsByUsername(
                user.getUsername()
        )) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        return ResponseEntity
                .ok()
                .body(new MessageResponse("OK"));
    }


}
