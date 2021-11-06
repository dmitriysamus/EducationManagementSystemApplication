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

    @OneToOne(fetch = FetchType.EAGER, orphanRemoval = true, cascade = {CascadeType.ALL})
    private Group group;

    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true, cascade = {CascadeType.ALL})
    Set<Lesson> lessons = new HashSet<>();

    public void addLesson (Lesson lesson) {
        this.lessons.add(lesson);
        lesson.setJournal(this);
    }
}
