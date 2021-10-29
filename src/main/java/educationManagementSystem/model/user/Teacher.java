package educationManagementSystem.model.user;

import educationManagementSystem.model.Role;
import educationManagementSystem.model.Token;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Модель учителя. Записывается в БД в таблицу с имененм teachers
 * реализует методы обучения интерфейса {@link Teach}
 * @author habatoo
 *
 * @param "id" - primary key таблицы teachers.
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
@Table(	name = "teachers",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username"),
                @UniqueConstraint(columnNames = "email")
        })
public class Teacher extends AbstractUser implements Teach {

        @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true)
        private Set<Group> groups = new HashSet<>();

        public Teacher(String username, String email, String password) {
                super(username, email, password);
        }

        /**
         * Метод {@link Teacher#addStudentToGroup} добавляет пользователя в группу {@link Group}
         *
         * @param user пользователь для добавления в группу
         * @param groupNum id группы
         */
        @Override
        public void addStudentToGroup(User user, Integer groupNum) {
                //Implementation
        }

        /**
         * Метод {@link Teacher#deleteStudentFromGroup} удаляет пользователя из группы {@link Group}
         *
         * @param user пользователь для удаления из группы
         * @param groupNum id группы
         */
        @Override
        public void deleteStudentFromGroup(User user, Integer groupNum) {
                //Implementation
        }

        /**
         * Метод {@link Teacher#rateStudent} оценивает пользователя {@link User}
         *
         * @param user пользователь для оценки
         * @param rate оценка пользователя
         */
        @Override
        public void rateStudent(User user, Integer rate) {
                //Implementation
        }
}
