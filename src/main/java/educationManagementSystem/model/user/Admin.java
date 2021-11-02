package educationManagementSystem.model.user;

import educationManagementSystem.model.Role;
import educationManagementSystem.model.Token;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.List;
import java.util.Set;

/**
 * Модель администратора. Записывается в БД в таблицу с имененм admins
 * реализует методы администрирования интерфейса {@link Administer}
 * @author habatoo, dmitriysamus
 *
 * @param "id" - primary key таблицы admins.
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
@Table(	name = "admins",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username"),
                @UniqueConstraint(columnNames = "email")
        })
public class Admin extends AbstractUser implements Administer{
        public Admin(String username, String email, String password) {
                super(username, email, password);
        }

        /**
         * Метод {@link Admin#createGroup} создает новую группу {@link Group}
         * и добавляет в нее пользователей List<User> users.
         *
         * @param groupNum номер группы
         */
        @Override
        public void createGroup(Integer groupNum) {
                //Implementation
        }

        /**
         * Метод {@link Admin#createGroup} удаляет группу {@link Group}
         * по id группы.
         *
         * @param groupNum id группы
         */
        @Override
        public void deleteGroup(Integer groupNum) {
                //Implementation
        }
}
