package educationManagementSystem;

import educationManagementSystem.controllers.UserController;
import educationManagementSystem.model.ERole;
import educationManagementSystem.model.Role;
import educationManagementSystem.model.Token;
import educationManagementSystem.model.user.User;
import educationManagementSystem.payload.responce.JwtResponse;
import educationManagementSystem.repository.TokenRepository;
import educationManagementSystem.repository.UserRepository;

import educationManagementSystem.security.jwt.TokenUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashSet;
import java.util.Set;

/**
 * Класс UserTest проводит тестирование публичных методов
 * объекта класса {@link User}, класса {@link UserRepository},
 * класса {@link UserController}, класса {@link Token}.
 */
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
        email = "admin@admin.com";
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

    /**
     * Метод тестирует инициализацию контекста.
     * Сценарий проверяет успешность создания
     * {@link UserRepository}
     * {@link UserController}
     * {@link TokenRepository}
     */
    @Test
    public void loadControllers_Test() {
        assertThat(userRepository).isNotNull();
        assertThat(userController).isNotNull();
        assertThat(tokenRepository).isNotNull();
    }

    /**
     * Метод тестирует создание объекта {@link Token} методом
     * {@link TokenUtils#makeToken(String, String)}.
     * Сценарий проверяет успешность создания
     * токенов по объекту {@link User}
     */
    @Test
    public void createUserToken_Test() {
        User user = userRepository.findByUsername(username).get();
        JwtResponse jwtResponse = tokenUtils.makeAuth(user.getUsername(), password);
        tokenUtils.makeToken(username, jwtResponse.getAccessToken());

        assertEquals(true, tokenRepository.existsByToken(jwtResponse.getAccessToken()));
    }

    /**
     * Метод тестирует создание у объекта {@link User}
     * ролей {@link Role} методом {@link Role#setName(ERole)}.
     * Сценарий проверяет наличие ролей у объекта {@link User}
     * ролей (ROLE_ADMIN, ROLE_TEACHER, ROLE_USER).
     */
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

        Assert.assertTrue(user.getRoles().contains(role_1));
        Assert.assertTrue(user.getRoles().contains(role_2));
        Assert.assertTrue(user.getRoles().contains(role_3));
    }

    /**
     * Метод тестирует отображение у объекта {@link User}
     * всех доступных ролей {@link Role}
     * Сценарий проверяет наличие ролей у объекта {@link User}
     * ролей (ROLE_ADMIN, ROLE_TEACHER, ROLE_USER).
     */
    @Test
    public void showAllUsersRoles_Test() {
        JwtResponse jwtResponse = tokenUtils.makeAuth(username, password);
        tokenUtils.makeToken(username, jwtResponse.getAccessToken());

        User user = userRepository.findByUsername(username).get();
        Assert.assertTrue(user.getAuthorities().toString().contains("ROLE_ADMIN"));
        Assert.assertTrue(user.getAuthorities().toString().contains("ROLE_TEACHER"));
        Assert.assertTrue(user.getAuthorities().toString().contains("ROLE_USER"));
    }

    /**
     * Метод тестирует редактирование у объекта {@link User}
     * данных username, email, password
     * при запросе типа PUT по адресу "/api/auth/users/{id}"
     * где id - индекс id редактируемого объекта.
     * Сценарий проверяет возможность редактирования своих полей
     * пользователем с ролью (ROLE_ADMIN).
     * возвращает сообщение "User data was update successfully!"
     */
    @Test
    public void changeMyAdminData_Test() throws Exception{
        String id = "1";
        JwtResponse jwtResponse = tokenUtils.makeAuth(username, password);
        tokenUtils.makeToken(username, jwtResponse.getAccessToken());

        this.mockMvc.perform(put("/api/auth/users/" + id)
                        .header("Authorization", "Bearer " + jwtResponse.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"username\": \"admin2\", \"email\": " +
                                "\"admin2@admin2.com\", \"password\": \"12345\" }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").value("User data was update successfully!"));

    }

    /**
     * Метод тестирует редактирование у объекта {@link User}
     * данных username, email, password
     * при запросе типа PUT по адресу "/api/auth/users/{id}"
     * где id - индекс id редактируемого объекта.
     * Сценарий проверяет возможность редактирования своих полей
     * пользователем с ролью (ROLE_TEACHER).
     * возвращает сообщение "User data was update successfully!"
     */
    @Test
    public void changeUserData_Test() throws Exception{
        String id = "2";
        JwtResponse jwtResponse = tokenUtils.makeAuth(username, password);
        tokenUtils.makeToken(username, jwtResponse.getAccessToken());

        this.mockMvc.perform(put("/api/auth/users/" + id)
                        .header("Authorization", "Bearer " + jwtResponse.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"username\": \"user2\", \"email\": " +
                                "\"user2@user2.com\", \"password\": \"12345\" }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").value("User data was update successfully!"));
    }

    /**
     * Метод тестирует редактирование у объекта {@link User}
     * данных username, email, password
     * при запросе типа PUT по адресу "/api/auth/users/{id}"
     * где id - индекс id редактируемого объекта.
     * Сценарий проверяет возможность редактирования своих полей
     * пользователем с ролью (ROLE_USER).
     * возвращает сообщение "User data was update successfully!"
     */
    @Test
    public void changeMyUserData_Test() throws Exception{
        JwtResponse jwtResponse = tokenUtils.makeAuth("user", password);
        tokenUtils.makeToken(username, jwtResponse.getAccessToken());

        this.mockMvc.perform(put("/api/auth/users/3")
                        .header("Authorization", "Bearer " + jwtResponse.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"username\": \"user2\", \"email\": " +
                                "\"user2@user2.com\", \"password\": \"12345\" }"))
                .andExpect(status().is(200))
                .andExpect(jsonPath("message").value("User data was update successfully!"));

    }

    /**
     * Метод тестирует редактирование у объекта {@link User}
     * данных username, email, password
     * при запросе типа PUT по адресу "/api/auth/users/{id}"
     * где id - индекс id редактируемого объекта.
     * Сценарий проверяет не возможность редактирования не своих полей
     * пользователем с ролью (ROLE_USER).
     * возвращает сообщение "You can edit only yourself data."
     */
    @Test
    public void changeNotMyUserData_Test() throws Exception{
        String id = "1";
        JwtResponse jwtResponse = tokenUtils.makeAuth("user", password);
        tokenUtils.makeToken(username, jwtResponse.getAccessToken());

        this.mockMvc.perform(put("/api/auth/users/" + id)
                        .header("Authorization", "Bearer " + jwtResponse.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"userName\": \"admin2\", \"email\": " +
                                "\"admin2@admin2.com\", \"password\": \"12345\" }"))
                .andExpect(status().is(400))
                .andExpect(jsonPath("message").value("You can edit only yourself data."));
    }

    /**
     * Метод тестирует удаление объекта {@link User}.
     * при запросе типа DELETE по адресу "/api/auth/users/{id}"
     * где id - индекс id удаляемого объекта.
     * Сценарий проверяет возможность удаление пользователя с id = 2
     * пользователем с ролью (ROLE_ADMIN).
     * возвращает сообщение "User was deleted successfully!"
     */
    @Test
    public void deleteUserByAdmin_Test() throws Exception{
        String id = "2";
        JwtResponse jwtResponse = tokenUtils.makeAuth(username, password);
        tokenUtils.makeToken(username, jwtResponse.getAccessToken());

        this.mockMvc.perform(delete("/api/auth/users/" + id)
                        .header("Authorization", "Bearer " + jwtResponse.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").value("User was deleted successfully!"));
    }

    /**
     * Метод тестирует удаление объекта {@link User}.
     * при запросе типа DELETE по адресу "/api/auth/users/{id}"
     * где id - индекс id удаляемого объекта.
     * Сценарий проверяет возможность удаление пользователя с id = 1
     * пользователем с ролью (ROLE_USER).
     * возвращает статус ответа 403.
     */
    @Test
    public void failDeleteUserByUser_Test() throws Exception{
        String id = "1";
        JwtResponse jwtResponse = tokenUtils.makeAuth("user", password);
        tokenUtils.makeToken(username, jwtResponse.getAccessToken());

        this.mockMvc.perform(delete("/api/auth/users/" + id)
                        .header("Authorization", "Bearer " + jwtResponse.getAccessToken()))
                .andExpect(status().is(403));
    }

    /**
     * Метод тестирует отображение всех объектов {@link User}.
     * при запросе типа GET по адресу "/api/auth/users/"
     * Сценарий проверяет возможность отображения всех пользователей
     * возвращает статус ответа 200.
     */
    @Test
    public void showAllUsers_Test() throws Exception{
        JwtResponse jwtResponse = tokenUtils.makeAuth(username, password);
        tokenUtils.makeToken(username, jwtResponse.getAccessToken());
        String resp = "[{\"roles\":[{\"id\":1,\"name\":\"ROLE_ADMIN\"},{\"id\":2,\"name\":\"ROLE_TEACHER\"},{\"id\":3,\"name\":\"ROLE_USER\"}],\"email\":\"admin@admin.com\",\"id\":1,\"username\":\"admin\"},{\"roles\":[{\"id\":2,\"name\":\"ROLE_TEACHER\"},{\"id\":3,\"name\":\"ROLE_USER\"}],\"email\":\"teacher@teacher.com\",\"id\":2,\"username\":\"teacher\"},{\"roles\":[{\"id\":3,\"name\":\"ROLE_USER\"}],\"email\":\"user@user.com\",\"id\":3,\"username\":\"user\"}]";

        this.mockMvc.perform(get("/api/auth/users/")
                        .header("Authorization", "Bearer " + jwtResponse.getAccessToken()))
                .andExpect(status().is(200))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andReturn().getResponse().getContentAsString().equals(resp);
    }

    /**
     * Метод тестирует обновление всех объектов {@link Token}.
     * при запросе типа DELETE по адресу "/api/auth/users/tokens"
     * Сценарий проверяет удаление всех объектов {@link Token} с
     * не валидной датой при отсутсвии объектов {@link Token} с
     * не валидной датой.
     * Возвращает сообщение "All tokens have valid expiry date!".
     */
    @Test
    public void tokensDataCheck_Test() throws Exception{
        JwtResponse jwtResponse = tokenUtils.makeAuth(username, password);
        tokenUtils.makeToken(username, jwtResponse.getAccessToken());

        this.mockMvc.perform(delete("/api/auth/users/tokens")
                        .header("Authorization", "Bearer " + jwtResponse.getAccessToken()))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("message").value("All tokens have valid expiry date!"));
    }

    /**
     * Метод тестирует обновление всех объектов {@link Token}.
     * при запросе типа DELETE по адресу "/api/auth/users/tokens"
     * Сценарий проверяет удаление всех объектов {@link Token} с
     * не валидной датой при наличии объектов {@link Token} с
     * не валидной датой.
     * Возвращает сообщение "Tokens with expiry date was deleted successfully!".
     */
    @Test
    public void tokensDataClean_Test() throws Exception{
        JwtResponse jwtResponse = tokenUtils.makeAuth(username, password);
        tokenUtils.makeToken(username, jwtResponse.getAccessToken());

        // Create old token
        Assert.assertEquals(1, tokenRepository.findAll().size());
        tokenUtils.makeOldToken(username, password);
        Assert.assertEquals(2, tokenRepository.findAll().size());

        this.mockMvc.perform(delete("/api/auth/users/tokens")
                        .header("Authorization", "Bearer " + jwtResponse.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").value(
                        "Tokens with expiry date was deleted successfully!"));

        Assert.assertEquals(1, tokenRepository.findAll().size());
    }

}
