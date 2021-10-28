package educationManagementSystem.model;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "roles")
@ToString(of = {"id", "name"})
@EqualsAndHashCode(of = {"id"})
public class Role implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ERole name;

    @Override
    public String getAuthority() {
        return name.getAuthority();
    }

}
