package educationManagementSystem.model.education;

import educationManagementSystem.model.ERole;
import educationManagementSystem.model.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "grades")
public class Grade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private EGrade name;

    @ManyToOne(fetch = FetchType.EAGER)
    private Lesson lesson;

    @ManyToOne(fetch = FetchType.EAGER)
    private User student;
}
