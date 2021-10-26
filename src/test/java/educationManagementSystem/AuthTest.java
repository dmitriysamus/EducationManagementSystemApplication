package educationManagementSystem;

import educationManagementSystem.controllers.AuthController;
import educationManagementSystem.model.user.User;
import educationManagementSystem.payload.responce.JwtResponse;
import educationManagementSystem.repository.UserRepository;
import educationManagementSystem.security.jwt.TokenUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
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

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = { "spring.config.location=classpath:application-test.yml" })
@Sql(value = {"/create-user-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/create-user-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class AuthTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthController authController;

    @Autowired
    TokenUtils tokenUtils;

    @Autowired
    private UserRepository userRepository;

    @Value("${habatoo.app.jwtSecret}")
    private String jwtSecret;

    @Value("${habatoo.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    String username = "admin";
    String password = "12345";

    @Test
    public void loadControllers() {
        assertThat(authController).isNotNull();
    }

    @Test
    public void createAdmin_Test() throws Exception{
        this.mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"userName\": \"testmod\", \"email\": \"testmod@mod.com\", \"password\": \"12345\", \"role\": [\"admin\", \"teacher\", \"user\"] }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").value("User registered successfully!"));

        User user = userRepository.findByUsername("testmod").get();
        JwtResponse jwtResponse = tokenUtils.makeAuth(user.getUsername(), password);
        tokenUtils.makeToken("testmod", jwtResponse.getAccessToken());
        System.out.println("AuthTest.testCreateAdmin " + user.getRoles().toString());
        Assert.assertTrue(user.getRoles().toString().contains("ROLE_ADMINISTRATOR"));
        Assert.assertTrue(user.getRoles().toString().contains("ROLE_USER"));
        Assert.assertTrue(user.getRoles().toString().contains("ROLE_MODERATOR"));
        Assert.assertTrue(user.getEmail().contains("testmod@mod.com"));
    }


    @Test
    public void testCreateUser_Test() throws Exception{
        this.mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"userName\": \"guest\", \"email\": \"guest@guest.com\", \"password\": \"12345\", \"role\": [\"user\"] }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").value("User registered successfully!"));

        User user = userRepository.findByUsername("guest").get();
        JwtResponse jwtResponse = tokenUtils.makeAuth(user.getUsername(), password);
        tokenUtils.makeToken("guest", jwtResponse.getAccessToken());
        Assert.assertFalse(user.getRoles().toString().contains("ROLE_ADMINISTRATOR"));
        Assert.assertTrue(user.getRoles().toString().contains("ROLE_USER"));
        Assert.assertFalse(user.getRoles().toString().contains("ROLE_MODERATOR"));
        Assert.assertTrue(user.getEmail().contains("guest@guest.com"));
    }

    @Test
    public void testCreateAdminAndUser_Test() throws Exception{
        this.mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"userName\": \"admin2\", \"email\": \"admin2@admin2.com\", \"password\": \"12345\", \"role\": [\"admin\"] }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").value("User registered successfully!"));

        User user = userRepository.findByUsername("admin2").get();
        JwtResponse jwtResponse = tokenUtils.makeAuth(user.getUsername(), password);
        tokenUtils.makeToken("admin2", jwtResponse.getAccessToken());
        Assert.assertTrue(user.getRoles().toString().contains("ROLE_ADMINISTRATOR"));
        Assert.assertFalse(user.getRoles().toString().contains("ROLE_USER"));
        Assert.assertFalse(user.getRoles().toString().contains("ROLE_MODERATOR"));
        Assert.assertTrue(user.getEmail().contains("admin2@admin2.com"));
    }

    @Test
    public void testCreateUsernameInDb_Test() throws Exception{
        this.mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"userName\": \"admin\", \"email\": \"admin2@admin2.com\", \"password\": \"12345\", \"role\": [\"admin\"] }"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("message").value("Error: Username is already taken!"));
    }

    @Test
    public void createEmailInDb_Test() throws Exception{
        this.mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"username\": \"admin2\", \"email\": \"admin@admin.com\", \"password\": \"12345\", \"role\": [\"admin\"] }"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("message").value("Error: Email is already in use!"));
    }

    @Test
    public void createRoleNotInDb_Test() throws Exception{
        this.mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"userName\": \"cat\", \"email\": \"cat@cat.com\", \"password\": \"12345\", \"role\": [\"cat\"] }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").value("User registered successfully!"));
    }

    @Test
    public void failCreateUserWithoutAdminRole_Test() throws Exception{
        this.mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"userName\": \"admin2\", \"email\": \"admin2@admin2.com\", \"password\": \"12345\", \"role\": [\"admin\"] }"))
                .andExpect(status().is(200))
                .andExpect(jsonPath("message").value("User registered successfully!"));
    }

    @Test
    public void loginForbiddenTest_Test() throws Exception{
        this.mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"userName\": \"mod\", \"password\": \"123456\" }"))
                .andExpect(status().isUnauthorized());
//                .andExpect(jsonPath("path").value(""))
//                .andExpect(jsonPath("error").value("Unauthorized"))
//                .andExpect(jsonPath("message").value("Bad credentials"))
//                .andExpect(jsonPath("status").value(401));
    }

    @Test
    public void adminLogin_Test() throws Exception{
        this.mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"userName\": \"admin\", \"password\": \"12345\" }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("admin"))
                .andExpect(jsonPath("$.userEmail").value("admin@admin.com"))
                .andExpect((jsonPath("$.roles", Matchers.containsInAnyOrder("ROLE_ADMINISTRATOR","ROLE_TEACHER", "ROLE_USER"))));
    }

    @Test
    public void userLogin_Test() throws Exception{
        this.mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"userName\": \"user\", \"password\": \"12345\" }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("user"))
                .andExpect(jsonPath("$.userEmail").value("user@user.com"))
                .andExpect((jsonPath("$.roles", Matchers.containsInAnyOrder("ROLE_USER"))));
    }

    @Test
    public void logoutFail_Test() throws Exception {
        this.mockMvc.perform(get("/api/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"token\": \"\" }"))
                .andExpect(status().isUnauthorized());
//                .andExpect(jsonPath("path").value(""))
//                .andExpect(jsonPath("error").value("Unauthorized"))
//                .andExpect(jsonPath("message").value("Full authentication is required to access this resource"))
//                .andExpect(jsonPath("status").value(401));

    }

    /**
     * Проверка метода logout, для корректной проверки требует токена с активным статусом и не истекшим сроком
     * @throws Exception
     */
    @Test
    public void logout_Test() throws Exception {
        JwtResponse jwtResponse = tokenUtils.makeAuth(username, password);
        tokenUtils.makeToken(username, jwtResponse.getAccessToken());

        this.mockMvc.perform(get("/api/auth/logout")
                        .header("Authorization", "Bearer " + jwtResponse.getAccessToken()))
                .andExpect(status().is(400))
                .andExpect(jsonPath("message").value("You are logout."));
    }

}
