package educationManagementSystem.model;

import org.springframework.security.core.GrantedAuthority;

/**
 * Перечень возможных ролей по доступу пользователя.
 * @see Role (таблица ролей).
 * @author habatoo
 */
public enum ERole implements GrantedAuthority {
    ROLE_USER,
    ROLE_TEACHER,
    ROLE_ADMIN;

    @Override
    public String getAuthority() {
        return name();
    }
}
