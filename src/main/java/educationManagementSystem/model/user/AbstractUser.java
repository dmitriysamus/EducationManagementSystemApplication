package educationManagementSystem.model.user;

import com.fasterxml.jackson.annotation.*;
import educationManagementSystem.model.Role;
import educationManagementSystem.model.Token;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@MappedSuperclass
@Getter
@Setter
public abstract class AbstractUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    private String username;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "userTokens", fetch = FetchType.EAGER, orphanRemoval = true)
    @JsonIdentityReference
    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id")
    @Column(name="USER_TOKENS")
    private Set<Token> tokens = new HashSet<>();

//    @Column(name="USER_EMAIL_ACTIVATION_STATUS")
//    private boolean activationEmailStatus;
//
//    @Column(name="USER_EMAIL_ACTIVATION_CODE")
//    private String activationEmailCode;

    @Column(name="USER_CREATION_DATE", updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime creationDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastVisitedDate;

    public AbstractUser() {
    }

    public AbstractUser (String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }


}
