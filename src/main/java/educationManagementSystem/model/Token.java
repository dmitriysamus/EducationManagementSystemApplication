package educationManagementSystem.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import educationManagementSystem.model.user.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Модель токенов пользователей с указанием статуса токена и срока его действия
 *
 * @author habatoo
 *
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "TOKENS")
@ToString(of = {"id", "token", "creationDate", "expiryDate", "active"})
@EqualsAndHashCode(of = {"id"})
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "TOKEN", nullable = false, unique = true, length = 500)
    private String token;

    @Column(name = "TOKEN_CREATION_DATE", updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime creationDate;

    @Column(name = "TOKEN_EXPIRY_DATE", updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expiryDate;

    @Column(name = "TOKEN_STATUS")
    private boolean active;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TOKEN_USER_ID")
    private User userTokens;

    public Token(String token, User userTokens) {
        this.token = token;
        this.userTokens = userTokens;
    }

}
