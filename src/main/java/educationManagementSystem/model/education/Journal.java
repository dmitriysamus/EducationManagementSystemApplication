package educationManagementSystem.model.education;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "journals")
public class Journal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.EAGER, orphanRemoval = true)
    private Group group;

    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true)
    Set<Lesson> lessons = new HashSet<>();

}
