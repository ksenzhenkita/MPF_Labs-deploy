package sumdu.edu.ua.core.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 128)
    private String email;

    @Column(length = 255)
    private String password;

    @Column(name = "nickname", length = 128)
    private String nickname;

    @Column(length = 32)
    private String role;

    @Column(nullable = false)
    private boolean verified;

    @Column(name = "verification_token", length = 255)
    private String verificationToken;
}
