package educationManagementSystem;

import educationManagementSystem.controllers.AuthController;
import educationManagementSystem.controllers.TestController;
import educationManagementSystem.model.User;
import educationManagementSystem.repository.UserRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserRepository userRepository;
    private AuthController authController;
    private TestController testController;

    String username;
    String email;
    String password;
    User user;

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

    }

    /**
     * Очистка экземпляров тестируемого класса {@link User}.
     */
    @AfterEach
    void tearDown() {

        userRepository.deleteAll();

    }

    @Test
    void getUser_Test() {

        userRepository.findById(1);
        Mockito.verify(userRepository).findById(1);

    }

    @Test
    void getAllUser_Test() {

        userRepository.findAll();
        Mockito.verify(userRepository).findAll();

    }

    @Test
    void addUser_Test() {

        // TODO
        Mockito.verify(userRepository).findById(1);

    }

    @Test
    void deleteUser_Test() {

        userRepository.deleteById(2);
        Mockito.verify(userRepository).findById(2);

    }
}
