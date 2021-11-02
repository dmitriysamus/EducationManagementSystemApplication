package educationManagementSystem.controllers;

import educationManagementSystem.model.ERole;
import educationManagementSystem.model.Role;
import educationManagementSystem.model.user.Group;
import educationManagementSystem.model.user.User;
import educationManagementSystem.payload.request.SignupRequest;
import educationManagementSystem.payload.responce.MessageResponse;
import educationManagementSystem.repository.GroupRepository;
import educationManagementSystem.repository.TokenRepository;
import educationManagementSystem.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * Контроллер работы с группами
 * @version 0.001
 * @author dmitriysamus
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth/groups")
public class GroupController {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final GroupRepository groupRepository;

    public GroupController(UserRepository userRepository, TokenRepository tokenRepository, GroupRepository groupRepository) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.groupRepository = groupRepository;
    }

    /**
     * @method createGroup - при http POST запросе по адресу .../api/auth/groups/{groupNum}
     * @param groupNum - номер группы
     * возвращает данные
     * @return {@code ResponseEntity.ok - Group created successfully!} - ок при успешном создании группы.
     * @return {@code ResponseEntity.badRequest - Error: Group already exists!} - ошибка при создании уже существующей группы.
     * @see ResponseEntity
     */
    @PostMapping("/{groupNum}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> createGroup(@PathVariable("groupNum") Integer groupNum) {
        Group group = new Group(groupNum);
        if (groupRepository.existsById(group.getGroupNum())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Group already exists!"));
        }
        groupRepository.save(group);
        return ResponseEntity.ok(new MessageResponse("Group created successfully!"));
    }

    /**
     * @method deleteGroup - при http DELETE запросе по адресу .../api/auth/groups/{groupNum}
     * @param groupNum - номер группы
     * возвращает данные
     * @return {@code ResponseEntity.ok - Group deleted successfully!} - ок при успешном удалении группы.
     * @return {@code ResponseEntity.badRequest - Error: Group does not exist!} - ошибка при удалении несуществующей группы.
     * @see ResponseEntity
     */
    @DeleteMapping("/{groupNum}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteGroup(@PathVariable("groupNum") Integer groupNum) {

        try {
            groupRepository.delete(groupRepository.findById(groupNum).get());
            return ResponseEntity.ok(new MessageResponse("Group deleted successfully!"));
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Group does not exist!"));
        }

    }

    /**
     * @method addGroupTeacher - при http POST запросе по адресу .../api/auth/groups/{groupNum}/{teacher}
     * @param groupNum - номер группы
     * @param teacherId - id учителя
     * возвращает данные
     * @return {@code ResponseEntity.ok - Teacher added successfully!} - ок при успешном добавлении учителя в группу.
     * @return {@code ResponseEntity.badRequest - Error: User (teacher) has not role teacher!} - ошибка при добавлении пользователя без роли teacher.
     * @return {@code ResponseEntity.badRequest - Error: Group does not exist!} - ошибка при добавлении учителя в несуществующую группу.
     * @return {@code ResponseEntity.badRequest - Error: User (teacher) does not exist!} - ошибка при добавлении несуществующего учителя в группу.
     * @see ResponseEntity
     */
    @PostMapping("/{groupNum}/{teacher}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> addGroupTeacher(@PathVariable("groupNum") Integer groupNum, @PathVariable("teacher") Integer teacherId) {
        if (!userRepository.existsById(teacherId)) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: User (teacher) does not exist!"));
        }
        if (!groupRepository.existsById(groupNum)) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Group does not exist!"));
        }

        User teacher = userRepository.findById(teacherId).get();
        Set<Role> teacherRoles = teacher.getRoles();
        boolean counter = false;
        for (Role role: teacherRoles) {
            if (role.getName().equals(ERole.ROLE_TEACHER)) {
                counter = true;
                break;
            }
        }
        if (!counter) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: User (teacher) has not role teacher!"));
        }

        Group group = groupRepository.findById(groupNum).get();
        group.setTeacher(teacher);
        groupRepository.save(group);
        return ResponseEntity.ok(new MessageResponse("Teacher added successfully!"));

    }

    /**
     * @method addStudentToGroup - при http POST запросе по адресу .../api/auth/groups/students/{groupNum}/{studentId}
     * @param groupNum - номер группы
     * @param studentId - id студента
     * возвращает данные
     * @return {@code ResponseEntity.ok - Student added successfully!} - ок при успешном добавлении студента в группу.
     * @return {@code ResponseEntity.badRequest - Error: User (user) has not role user!} - ошибка при добавлении пользователя без роли user.
     * @return {@code ResponseEntity.badRequest - Error: Group does not exist!} - ошибка при добавлении студента в несуществующую группу.
     * @return {@code ResponseEntity.badRequest - Error: User (user) does not exist!} - ошибка при добавлении несуществующего студента в группу.
     * @see ResponseEntity
     */
    @PostMapping("students/{groupNum}/{studentId}")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public ResponseEntity<?> addStudentToGroup(@PathVariable("groupNum") Integer groupNum, @PathVariable("studentId")  Integer studentId) {

        if (!userRepository.existsById(studentId)) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: User (user) does not exist!"));
        }
        if (!groupRepository.existsById(groupNum)) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Group does not exist!"));
        }

        User student = userRepository.findById(studentId).get();
        Set<Role> studentRoles = student.getRoles();
        boolean counter = false;
        for (Role role: studentRoles) {
            if (role.getName().equals(ERole.ROLE_USER)) {
                counter = true;
                break;
            }
        }
        if (!counter) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: User (user) has not role user!"));
        }

        Group group = groupRepository.findById(groupNum).get();
        Set<User> students = group.getUsers();

        students.add(student);
        groupRepository.save(group);
        return ResponseEntity.ok(new MessageResponse("Student added successfully!"));

    }

    /**
     * @method deleteStudentFromGroup - при http DELETE запросе по адресу .../api/auth/groups/students/{groupNum}/{studentId}
     * @param groupNum - номер группы
     * @param studentId - id студента
     * возвращает данные
     * @return {@code ResponseEntity.ok - Student deleted successfully!} - ок при успешном удалении студента из группы.
     * @return {@code ResponseEntity.badRequest - Error: User (user) does not exist in the group!} - ошибка при удалении несуществующего в группу студента.
     * @return {@code ResponseEntity.badRequest - Error: Group does not exist!} - ошибка при добавлении студента в несуществующую группу.
     * @see ResponseEntity
     */
    @DeleteMapping("students/{groupNum}/{studentId}")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public ResponseEntity<?> deleteStudentFromGroup(@PathVariable("groupNum") Integer groupNum, @PathVariable("studentId") Integer studentId) {

        if (!groupRepository.existsById(groupNum)) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Group does not exist!"));
        }

        Group group = groupRepository.findById(groupNum).get();
        Set<User> users = group.getUsers();
        if (!users.contains(userRepository.findById(studentId).get())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: User (user) does not exist in the group!"));
        }

        User student = userRepository.findById(studentId).get();
        users.remove(student);
        group.setUsers(users);
        groupRepository.save(group);
        return ResponseEntity.ok(new MessageResponse("Student deleted successfully!"));

    }

}
