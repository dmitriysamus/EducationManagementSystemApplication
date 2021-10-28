package educationManagementSystem.controllers;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import educationManagementSystem.model.ERole;
import educationManagementSystem.model.Role;
import educationManagementSystem.model.Token;
import educationManagementSystem.model.user.User;
import educationManagementSystem.payload.request.LoginRequest;
import educationManagementSystem.payload.request.SignupRequest;
import educationManagementSystem.repository.TokenRepository;
import educationManagementSystem.security.jwt.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import educationManagementSystem.payload.responce.JwtResponse;
import educationManagementSystem.payload.responce.MessageResponse;
import educationManagementSystem.repository.RoleRepository;
import educationManagementSystem.repository.UserRepository;
import educationManagementSystem.security.jwt.JwtUtils;
import educationManagementSystem.security.services.UserDetailsImpl;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    TokenRepository tokenRepository;

    @Autowired
    TokenUtils tokenUtils;

    @Value("${habatoo.app.secretKey}")
    private String secretKey;

    @Value("${habatoo.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    /**
     * @method authenticateUser - при http post запросе по адресу .../api/auth/login
     * @param loginRequest - запрос на доступ с параметрами user login+password.
     * возвращает
     * @return {@code ResponseEntity ответ}
     * @see LoginRequest
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        JwtResponse jwtResponse = tokenUtils.makeAuth(loginRequest.getUsername(), loginRequest.getPassword());
        tokenUtils.makeToken(loginRequest.getUsername(), jwtResponse.getAccessToken());
        User user = userRepository.findByUsername(loginRequest.getUsername()).get();
        user.setLastVisitedDate(LocalDateTime.now());
        userRepository.save(user);
        return ResponseEntity.ok(jwtResponse);
    }

    /**
     * @method registerUser - при http POST запросе по адресу .../api/auth/register
     * @param signUpRequest - входные данные по текущему аутентифицированному пользователю
     * возвращает данные
     * @return {@code ResponseEntity.ok - User registered successfully!} - ок при успешной регистрации.
     * @return {@code ResponseEntity.badRequest - Error: Role is not found.} - ошибка при указании неправильной роли.
     * @return {@code ResponseEntity.badRequest - Error: Username is already taken!} - ошибка при дублировании username при регистрации.
     * @return {@code ResponseEntity.badRequest - Error: Email is already in use!} - ошибка при дублировании email при регистрации.
     * @see ResponseEntity
     * @see SignupRequest
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest, HttpServletRequest request) {

        if (userRepository.existsByUsername(
                signUpRequest.getUsername()
        )) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        User user = new User(
                signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword())
        );

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);

                        break;
                    case "teacher":
                        Role modRole = roleRepository.findByName(ERole.ROLE_TEACHER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);

                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        user.setCreationDate(LocalDateTime.now());
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    /**
     * @method logoutUser - при http post запросе по адресу .../api/auth/logout
     * @param request - запрос на выход с параметрами user login+password + токен jwt.
     * возвращает
     * @return {@code ResponseEntity ответ}
     * @see LoginRequest
     */
    @GetMapping("/logout")
    @PreAuthorize("hasRole('ROLE_TEACHER') or hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> logoutUser(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        String jwt = headerAuth.substring(7, headerAuth.length());

        Token unActiveToken = tokenRepository.findByToken(jwt);
        unActiveToken.setActive(false);
        tokenRepository.save(unActiveToken);

        return ResponseEntity
                .badRequest()
                .body(new MessageResponse("You are logout."));
    }

}