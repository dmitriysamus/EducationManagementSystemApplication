package educationManagementSystem;

import educationManagementSystem.controllers.GroupController;
import educationManagementSystem.controllers.UserController;
import educationManagementSystem.model.education.Group;

import educationManagementSystem.payload.responce.JwtResponse;
import educationManagementSystem.repository.GroupRepository;
import educationManagementSystem.repository.LessonRepository;
import educationManagementSystem.repository.UserRepository;
import educationManagementSystem.security.jwt.TokenUtils;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = { "spring.config.location=classpath:application-test.yml" })
@Sql(value = {"/create-user-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/create-user-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class GroupTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupController groupController;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    TokenUtils tokenUtils;
    Integer groupNum;

    String usernameAdmin;
    String emailAdmin;
    String passwordAdmin;

    String usernameTeacher;
    String emailTeacher;
    String passwordTeacher;

    /**
     * Инициализация экземпляров тестируемого класса {@link Group}.
     */
    @BeforeEach
    void setUp() {
        groupNum = 123;

        usernameAdmin = "admin";
        emailAdmin = "admin@admin.com";
        passwordAdmin = "12345";

        usernameTeacher = "teacher";
        emailTeacher = "teacher@teacher.com";
        passwordTeacher = "12345";

    }

    /**
     * Очистка экземпляров тестируемого класса {@link Group}.
     */
    @AfterEach
    void tearDown() {
        groupRepository.deleteAll();
    }

    /**
     * Метод тестирует инициализацию контекста.
     * Сценарий проверяет успешность создания
     * {@link UserRepository}
     * {@link UserController}
     * {@link GroupRepository}
     */
    @Test
    public void loadControllers_Test() {
        assertThat(userRepository).isNotNull();
        assertThat(groupController).isNotNull();
        assertThat(groupRepository).isNotNull();
        assertThat(lessonRepository).isNotNull();
    }

    /**
     * Метод тестирует создание объекта {@link Group}
     * Сценарий проверяет успешность создания объекта {@link Group}.
     */
    @Test
    public void createGroup_Test() throws Exception {

        JwtResponse jwtResponse = tokenUtils.makeAuth(usernameAdmin, passwordAdmin);
        tokenUtils.makeToken(usernameAdmin, jwtResponse.getAccessToken());
        this.mockMvc.perform(post("/api/auth/groups/" + groupNum)
                .header("Authorization", "Bearer " + jwtResponse.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").value("Group created successfully!"));

        Group group = groupRepository.findById(groupNum).get();


        Assert.assertEquals(groupNum, group.getGroupNum());

    }

    /**
     * Метод тестирует удаление объекта {@link Group}
     * Сценарий проверяет успешность удаления объекта {@link Group}.
     */
    @Test
    public void deleteGroup_Test() throws Exception {

        JwtResponse jwtResponse = tokenUtils.makeAuth(usernameAdmin, passwordAdmin);
        tokenUtils.makeToken(usernameAdmin, jwtResponse.getAccessToken());

        this.mockMvc.perform(delete("/api/auth/groups/" + 999)
                .header("Authorization", "Bearer " + jwtResponse.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").value("Group deleted successfully!"));
    }

    /**
     * Метод тестирует добавление в объект {@link Group} поля teacher
     * Сценарий проверяет успешность обновления объекта {@link Group}.
     */
    @Test
    public void addGroupTeacher_Test() throws Exception {

        JwtResponse jwtResponse = tokenUtils.makeAuth(usernameAdmin, passwordAdmin);
        tokenUtils.makeToken(usernameAdmin, jwtResponse.getAccessToken());

        this.mockMvc.perform(post("/api/auth/groups/" + 999 + "/" + 2)
                .header("Authorization", "Bearer " + jwtResponse.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").value("Teacher added successfully!"));

        Assert.assertEquals(2, groupRepository.findById(999).get().getTeacher().getId().longValue());
    }

    /**
     * Метод тестирует добавление в объект {@link Group} студента
     * Сценарий проверяет успешность обновления объекта {@link Group}.
     */
    @Test
    public void addStudentToGroup_Test() throws Exception {

        JwtResponse jwtResponse = tokenUtils.makeAuth(usernameAdmin, passwordAdmin);
        tokenUtils.makeToken(usernameAdmin, jwtResponse.getAccessToken());

        this.mockMvc.perform(post("/api/auth/groups/students/" + 999 + "/" + 3)
                .header("Authorization", "Bearer " + jwtResponse.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").value("Student added successfully!"));

        Assert.assertEquals(userRepository.findById(3).get(), groupRepository.findById(999).get().getUsers().toArray()[0]);
    }

    /**
     * Метод тестирует удаление из объекта {@link Group} студента
     * Сценарий проверяет успешность обновления объекта {@link Group}.
     */
    @Test
    public void deleteStudentFromGroup_Test() throws Exception {

        JwtResponse jwtResponse = tokenUtils.makeAuth(usernameTeacher, passwordTeacher);
        tokenUtils.makeToken(usernameTeacher, jwtResponse.getAccessToken());

        this.mockMvc.perform(post("/api/auth/groups/students/" + 999 + "/" + 3)
                .header("Authorization", "Bearer " + jwtResponse.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").value("Student added successfully!"));

        this.mockMvc.perform(delete("/api/auth/groups/students/" + 999 + "/" + 3)
                .header("Authorization", "Bearer " + jwtResponse.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").value("Student deleted successfully!"));

        Assert.assertFalse(groupRepository.findById(999).get().getUsers().contains(userRepository.findById(3).get()));
    }

    @Test
    public void createLesson_Test() throws Exception {

        JwtResponse jwtResponse = tokenUtils.makeAuth(usernameAdmin, passwordAdmin);
        tokenUtils.makeToken(usernameAdmin, jwtResponse.getAccessToken());

        this.mockMvc.perform(post("/api/auth/groups/" + 999 + "/lesson")
                .header("Authorization", "Bearer " + jwtResponse.getAccessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"name\": \"Lesson Test\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").value("Lesson created successfully!"));

        Assert.assertEquals("Lesson Test", groupRepository.findById(999).get().getJournal().getLessons().stream().findFirst().get().getName());
        Assert.assertEquals(999, lessonRepository.findByName("Lesson Test").get().getJournal().getGroup().getGroupNum().intValue());
    }

}