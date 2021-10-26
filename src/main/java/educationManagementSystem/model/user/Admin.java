package educationManagementSystem.model.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(	name = "admins",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username"),
                @UniqueConstraint(columnNames = "email")
        })
public class Admin extends AbstractUser implements Administer{
        public Admin(String username, String email, String password) {
                super(username, email, password);
        }

        @Override
        public void createGroup(List<User> users, Integer groupNum) {
                //Implementation
        }

        @Override
        public void deleteGroup(Integer groupNum) {
                //Implementation
        }
}
