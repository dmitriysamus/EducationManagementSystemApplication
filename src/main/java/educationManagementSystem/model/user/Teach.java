package educationManagementSystem.model.user;

import java.util.List;

public interface Teach {
    void addStudentToGroup (User user, Integer groupNum);
    void deleteStudentFromGroup (User user, Integer groupNum);
    void rateStudent (User user, Integer rate);
}
