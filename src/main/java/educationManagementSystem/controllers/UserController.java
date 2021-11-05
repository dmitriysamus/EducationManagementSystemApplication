package educationManagementSystem.controllers;

import educationManagementSystem.model.Role;
import educationManagementSystem.model.Token;
import educationManagementSystem.model.user.User;
import educationManagementSystem.payload.responce.MessageResponse;
import educationManagementSystem.repository.GroupRepository;
import educationManagementSystem.repository.RoleRepository;
import educationManagementSystem.repository.TokenRepository;
import educationManagementSystem.repository.UserRepository;
import educationManagementSystem.security.jwt.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Контроллер работы с пользователями.
 * @version 0.001
 * @author habatoo
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth/users")
public class UserController {
    @Value("${habatoo.app.remoteAddr}")
    private String remoteAddr;

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final GroupRepository groupRepository;

    @Autowired
    public UserController(UserRepository userRepository,
                          TokenRepository tokenRepository,
                          GroupRepository groupRepository
    ) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.groupRepository = groupRepository;
    }

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    UserUtils userUtils;

    /**
     * @method userList - при http GET запросе по адресу .../api/auth/users
     * @return {@code List<user>} - список всех пользователей с полными данными пользователей.
     * @see User
     * @see Role
     */
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TEACHER')")
    @ResponseBody
    public ResponseEntity<?> userList() {
        List<Object> usersReturn = new ArrayList<>();
        List<User> usersCurrent = userRepository.findAll();
        for(User user: usersCurrent) {
            Map<String, Object> temp = new HashMap<String, Object>();
            temp.put("roles", user.getRoles());
            temp.put("email", user.getEmail());
            temp.put("username", user.getUsername());
            temp.put("id", user.getId());
            usersReturn.add(temp);
        }

        return ResponseEntity.ok(usersReturn);
    }

    /**
     * @method getUserInfo - при http GET запросе по адресу .../api/auth/users/getUserInfo
     * @param authentication - данные по текущему аутентифицированному пользователю
     * возвращает данные
     * @return {@code userRepository} - полные данные пользователя - user.userName, user.userEmail, user.roles
     * @see UserRepository
     */
    @GetMapping("/getUserInfo")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN') or hasRole('ROLE_TEACHER')")
    @ResponseBody
    public ResponseEntity<?>  getUserInfo(Authentication authentication) {
        Optional optionalUser = userRepository.findByUsername(authentication.getName());
        if(optionalUser.isPresent()) {
            return ResponseEntity.ok(optionalUser.get());
        }
        return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: can not find user data."));
    }

    /**
     * @method changeUser - при http PUT запросе по адресу .../api/auth/users/{id}
     * {id} - входные данные - id пользователя, данные которого редактируются, id не редактируетс
     * возвращает данные
     * @return - измененные данные пользователя, id изменению не подлежит.
     * @param userFromDb - данные пользователя отредактированные из формы
     * @param user - текущие данные пользователя
     * @see UserRepository
     */
    @PutMapping("{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TEACHER') or hasRole('ROLE_USER')")
    public ResponseEntity<?> changeUser(
            @PathVariable("id") User userFromDb,
            @RequestBody User user,
            Authentication authentication) {

        userFromDb = userRepository.findById(userFromDb.getId()).get();
        // check ID current user = ID edit user
        if(!(userFromDb.getId() == userRepository.findByUsername(authentication.getName()).get().getId())) {
            // admin check
            if(userRepository.findByUsername(authentication.getName()).get().getRoles().size() == 3) {
                return userUtils.checkUserNameAndEmail(user, userFromDb);
            }
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("You can edit only yourself data."));
        } else {
            return userUtils.checkUserNameAndEmail(user, userFromDb);
        }

    }

    /**
     * @method deleteUser - при http DELETE запросе по адресу .../api/auth/users/{id}
     * {id} - входные данные - id пользователя, данные которого удаляются.
     * @param user - обьект пользователя для удаления.
     * @see UserRepository
     */
    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable("id") User user) {
        try {
            userRepository.delete(user);
            return ResponseEntity.ok(new MessageResponse("User was deleted successfully!"));
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: User was not deleted!"));
        }
    }

    /**
     * @method clearTokens - при http DELETE запросе по адресу .../api/auth/users/tokens - очищает базу от токенов с истекшим сроком
     * @return {@code ResponseEntity.badRequest - All tokens have valid expiry date!} - если все токены имеют не истекший срок действия.
     * @return {@code ResponseEntity.badRequest - Error: Can't read token data!} - ошибка при запросе к таблице token.
     * @return {@code ResponseEntity.ok - Tokens with expiry date was deleted successfully!} - при успешном удалении токенов с истекшим сроком действия.
     */
    @DeleteMapping("/tokens")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> clearTokens() {

        try {
            List<Token> tokens = tokenRepository.findByExpiryDateBefore(LocalDateTime.now());
            if(tokens.isEmpty()) {
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("All tokens have valid expiry date!"));
            }
            else {
                for (Token token : tokens) {
                    try { tokenRepository.deleteById(token.getId()); } catch (Exception e) {
                        return ResponseEntity
                                .badRequest()
                                .body(new MessageResponse("Error: Can't delete token!"));
                    }
                }
                return ResponseEntity.ok(new MessageResponse("Tokens with expiry date was deleted successfully!"));
            }

        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Can't read token data!"));
        }

    }



}
