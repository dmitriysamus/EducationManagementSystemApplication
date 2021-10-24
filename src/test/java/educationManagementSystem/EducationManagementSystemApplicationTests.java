package educationManagementSystem;

import educationManagementSystem.model.ERole;
import educationManagementSystem.model.Role;
import educationManagementSystem.model.User;
import educationManagementSystem.repository.RoleRepository;
import educationManagementSystem.repository.UserRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.junit.runner.RunWith;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Класс для тестирования public методов {@link UserRepository}.
 *
 * @author habatoo
 */
@DataJpaTest
@RunWith(SpringRunner.class)
@TestPropertySource(properties = { "spring.config.location=classpath:application-test.yml" })
@Sql(value = {"/create-user-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/create-user-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class EducationManagementSystemApplicationTests {

    String username;
    String email;
    String password;
    User user;
    Role userRole;
    Set<Role> roles;

    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;

    /**
     * Инициализация экземпляров тестируемого класса {@link User}.
     */
    @BeforeEach
    void setUp() {

        username = "test";
        email = "test@test.com";
        password = "password";

        User user = new User(
                username,
                email,
                password
        );

        userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);

    }

    /**
     * Очистка экземпляров тестируемого класса {@link User}.
     */
    @AfterEach
    void tearDown() {

        userRepository.deleteAll();
        user = null;
        userRole = null;

    }

    @Test
    public void loadControllers() {

        assertThat(userRepository).isNotNull();
        assertThat(roleRepository).isNotNull();

    }

    @Test
    void itShouldCheckIfUserExists_Test() {

        userRepository.save(user);
        boolean expected = userRepository.existsByUsername(username);         // when
        assertThat(expected).isTrue(); //then

    }

    @Test
    void itShouldCheckIfUserDoesNotExists_Test() {

        boolean expected = userRepository.existsByUsername(username); // when
        assertThat(expected).isFalse();         //then

    }

}