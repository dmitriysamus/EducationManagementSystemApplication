package educationManagementSystem.model.user;

import educationManagementSystem.model.Role;
import educationManagementSystem.model.Token;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.Collection;

/**
 * Модель пользователя. Записывается в БД в таблицу с имененм users.
 * @author habatoo
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

    @ManyToOne(fetch = FetchType.EAGER)
    private Group group;

    public User(String username, String email, String password) {
        super(username, email, password);
    }

}