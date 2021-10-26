package educationManagementSystem.model.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(	name = "teachers",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username"),
                @UniqueConstraint(columnNames = "email")
        })
public class Teacher extends AbstractUser implements Teach {

        @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true)
        private Set<Group> groups = new HashSet<>();

        public Teacher(String username, String email, String password) {
                super(username, email, password);
        }

        @Override
        public void addStudentToGroup(User user, Integer groupNum) {
                //Implementation
        }

        @Override
        public void deleteStudentFromGroup(User user, Integer groupNum) {
                //Implementation
        }

        @Override
        public void rateStudent(User user, Integer rate) {
                //Implementation
        }
}
