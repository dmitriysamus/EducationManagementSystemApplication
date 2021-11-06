package educationManagementSystem.controllers;

import educationManagementSystem.model.ERole;
import educationManagementSystem.model.Role;
import educationManagementSystem.model.education.*;
import educationManagementSystem.model.user.User;
import educationManagementSystem.payload.request.GradeRequest;
import educationManagementSystem.payload.request.LessonRequest;
import educationManagementSystem.payload.responce.MessageResponse;
import educationManagementSystem.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
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
    private final LessonRepository lessonRepository;
    private final JournalRepository journalRepository;

    public GroupController(UserRepository userRepository,
                           TokenRepository tokenRepository,
                           GroupRepository groupRepository,
                           LessonRepository lessonRepository,
                           JournalRepository journalRepository) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.groupRepository = groupRepository;
        this.lessonRepository = lessonRepository;
        this.journalRepository = journalRepository;
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

    @PostMapping("{groupNum}/lesson")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public ResponseEntity<?> createLesson (@PathVariable("groupNum") Integer groupNum, @Valid @RequestBody LessonRequest lessonRequest) {

        if (!groupRepository.existsById(groupNum)) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Group does not exist!"));
        }

        Group group = groupRepository.findById(groupNum).get();
        Lesson lesson = new Lesson(lessonRequest.getName());
        Set<Lesson> lessons = new HashSet<>();

        if (group.getJournal() != null) {
            if (group.getJournal().getLessons().iterator().hasNext()) {
                lessons.addAll(group.getJournal().getLessons());
                Boolean counter = false;
                for (Lesson les: lessons) {
                    if (les.getName().equals(lesson.getName())) {
                        counter = true;
                        break;
                    }
                }

                if (counter) {
                    return ResponseEntity
                            .badRequest()
                            .body(new MessageResponse("Error: Lesson exists in the group!"));
                }
            }
        } else {
            group.addJournal(new Journal());
        }

        group.getJournal().addLesson(lesson);

        groupRepository.save(group);

        return ResponseEntity.ok(new MessageResponse("Lesson created successfully!"));
    }

    //have to be rewrite (set to add)
    @PostMapping("lessons/{lessonId}")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public ResponseEntity<?> createTask (@PathVariable("lessonId") Integer lessonId,
                                         @Valid @RequestBody LessonRequest lessonRequest) {

        if (!lessonRepository.existsById(lessonId)) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Lesson does not exist!"));
        }

        Lesson lesson = lessonRepository.findById(lessonId).get();

        Set<Task> tasks = lesson.getTasks();

        tasks.add(new Task(lessonRequest.getName()));

        lesson.setTasks(tasks);

        lessonRepository.save(lesson);

        return ResponseEntity.ok(new MessageResponse("Task created successfully!"));
    }

    //have to be rewrite (set to add)
    @PostMapping("rate/{lessonId}")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public ResponseEntity<?> rateStudent (@PathVariable("lessonId") Integer lessonId,
                                         @Valid @RequestBody GradeRequest gradeRequest) {

        if (!lessonRepository.existsById(lessonId)) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Lesson does not exist!"));
        }

        if (userRepository.existsById(gradeRequest.getStudentId())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: User does not exist!"));
        }

        User student = userRepository.findById(gradeRequest.getStudentId()).get();
        Lesson lesson = lessonRepository.findById(lessonId).get();

        Set<User> students = lesson.getJournal().getGroup().getUsers();

        Boolean counter = false;
        for (User user: students) {
            if (student.getId().equals(gradeRequest.getStudentId())) {
                counter = true;
                break;
            }
        }

        if (!counter) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Student does not exists in the group!"));
        }

        Set<Grade> grades = lesson.getGrades();

        Grade grade = new Grade();

        switch (gradeRequest.getGrade()) {
            case "pass":
                grade.setName(EGrade.PASS);
                break;
            case "fail":
                grade.setName(EGrade.FAIL);
                break;
            default:
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Error: Incorrect grade!"));
        }

        grade.setStudent(student);

        grades.add(grade);

        lesson.setGrades(grades);

        lessonRepository.save(lesson);

        return ResponseEntity.ok(new MessageResponse("Student rated successfully!"));
    }
}
