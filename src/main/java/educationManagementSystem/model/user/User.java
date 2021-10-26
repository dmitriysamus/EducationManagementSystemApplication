package educationManagementSystem.model.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(	name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username"),
                @UniqueConstraint(columnNames = "email")
        })
public class User extends AbstractUser {

    @ManyToOne(fetch = FetchType.LAZY)
    private Group group;

    public User(String username, String email, String password) {
        super(username, email, password);
    }

}