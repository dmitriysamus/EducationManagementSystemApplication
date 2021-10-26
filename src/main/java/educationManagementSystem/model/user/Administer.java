package educationManagementSystem.model.user;

import java.util.List;

public interface Administer {
    void createGroup (List<User> users, Integer groupNum);
    void deleteGroup (Integer groupNum);
}
