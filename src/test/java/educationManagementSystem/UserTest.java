package educationManagementSystem;

import educationManagementSystem.controllers.AuthController;
import educationManagementSystem.controllers.TestController;
import educationManagementSystem.controllers.UserController;
import educationManagementSystem.model.ERole;
import educationManagementSystem.model.Role;
import educationManagementSystem.model.User;
import educationManagementSystem.payload.responce.JwtResponse;
import educationManagementSystem.repository.TokenRepository;
import educationManagementSystem.repository.UserRepository;

import educationManagementSystem.security.jwt.TokenUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = { "spring.config.location=classpath:application-test.yml" })
@Sql(value = {"/create-user-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/create-user-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@ExtendWith(MockitoExtension.class)
class UserTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    TokenRepository tokenRepository;

    @Autowired
    private UserController userController;

    @Autowired
    TokenUtils tokenUtils;

    @Value("${habatoo.app.jwtSecret}")
    private String jwtSecret;

    @Value("${habatoo.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    String username;
    String email;
    String password;
    User user;

    /**
     * Инициализация экземпляров тестируемого класса {@link User}.
     */
    @BeforeEach
    void setUp() {

        username = "admin";
        email = "test@test.com";
        password = "12345";

        User user = new User(
                username,
                email,
                password
        );

    }

    /**
     * Очистка экземпляров тестируемого класса {@link User}.
     */
    @AfterEach
    void tearDown() {

        userRepository.deleteAll();

    }

    @Test
    public void loadControllers_Test() {
        assertThat(userRepository).isNotNull();
        assertThat(userController).isNotNull();
        assertThat(tokenRepository).isNotNull();
    }

    @Test
    public void createNewUser_Test() {
        User user = userRepository.findByUsername("user").get();

        assertEquals("user", user.getUsername());
        assertEquals("user@user.com", user.getEmail());
    }

    @Test
    public void createNewTeacher_Test() {
        User user = userRepository.findByUsername("teacher").get();

        assertEquals("teacher", user.getUsername());
        assertEquals("teacher@teacher.com", user.getEmail());

    }

    @Test
    public void createNewAdmin_Test() {
        User user = userRepository.findByUsername("admin").get();

        assertEquals("admin", user.getUsername());
        assertEquals("admin@admin.com", user.getEmail());
    }

    @Test
    public void createUserToken_Test() {
        User user = userRepository.findByUsername(username).get();
        JwtResponse jwtResponse = tokenUtils.makeAuth(user.getUsername(), password);
        tokenUtils.makeToken(username, jwtResponse.getAccessToken());

        assertEquals(true, tokenRepository.existsByToken(jwtResponse.getAccessToken()));
    }

    @Test
    public void createUserRole_Test() {
        User user = userRepository.findByUsername(username).get();
        Set<Role> mainRole = new HashSet<>();
        Role role_1 = new Role();
        role_1.setId(1);
        role_1.setName(ERole.ROLE_ADMIN);
        Role role_2 = new Role();
        role_2.setName(ERole.ROLE_TEACHER);
        role_2.setId(2);
        Role role_3 = new Role();
        role_3.setName(ERole.ROLE_USER);
        role_3.setId(3);
        mainRole.add(role_1);
        mainRole.add(role_2);
        mainRole.add(role_3);
        user.setRoles(mainRole);

        Assert.assertTrue(user.getRoles().toString().contains("ROLE_ADMIN"));
        Assert.assertTrue(user.getRoles().toString().contains("ROLE_TEACHER"));
        Assert.assertTrue(user.getRoles().toString().contains("ROLE_USER"));
    }
}
