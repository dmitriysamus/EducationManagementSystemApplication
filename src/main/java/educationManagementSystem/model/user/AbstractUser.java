package educationManagementSystem.model.user;

import com.fasterxml.jackson.annotation.*;
import educationManagementSystem.model.education.Group;
import educationManagementSystem.model.Role;
import educationManagementSystem.model.Token;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Модель абстрактного пользователя. Записывается в БД в таблицу с имененм users.
 * @author habatoo, dmitriysamus
 *
 * @param "id" - primary key таблицы users.
 * @param "username" - имя пользователя - предпоалагается строковоя переменная Имя + Фамилия.
 * @param "password" - пароль, в БД хранится в виде хешированном виде.
 * @param "email" - email пользователя.
 * @param "creationDate" - дата создания пользователя.
 * @param "roles" - роли пользователя - определяют возможности доступа - администратор, учитель, пользователь
 * @see Role
 * @param "token" - токен сессии пользователя
 * @see Token (токены пользователя).
 */
@MappedSuperclass
@Getter
@Setter
@ToString(of = {"id", "username", "password", "email", "creationDate"})
@EqualsAndHashCode(of = {"id"})
public abstract class AbstractUser implements Serializable, UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @NotBlank
    String username;

    @NotBlank
    @Email
    String email;

    @NotBlank
    String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "userTokens", fetch = FetchType.EAGER, orphanRemoval = true)
    @JsonIdentityReference
    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id")
    @Column(name="USER_TOKENS")
    Set<Token> tokens = new HashSet<>();

    @Column(name="USER_CREATION_DATE", updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime creationDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime lastVisitedDate;

    //Для user
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "GROUP_ID")
    private Group group;

    //Для teacher
    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true, cascade = {CascadeType.ALL})
    Set<Group> groups = new HashSet<>();

    public AbstractUser() {
    }

    /**
     * Конструктор для создания пользователя.
     * @param username - имя пользователя - предпоалагается строковоя переменная Имя + Фамилия.
     * @param email - email пользователя.
     * @param password - пароль, в БД хранится в виде хешированном виде.
     * activationStatus - поле подтверждения email пользователя.
     *
     */
    public AbstractUser(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.creationDate = LocalDateTime.now();

    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return !getcreationDate().equals(null);
    }

    public LocalDateTime getcreationDate() {
        return creationDate;
    }

}
