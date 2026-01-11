package sumdu.edu.ua.core.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sumdu.edu.ua.core.domain.User;
import sumdu.edu.ua.core.dto.RegisterUserDto;
import sumdu.edu.ua.core.port.MailPort;
import sumdu.edu.ua.core.port.UserRepositoryPort;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepositoryPort users;
    private final MailPort mailPort;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepositoryPort users,
                       MailPort mailPort) {
        this.users = users;
        this.mailPort = mailPort;
    }

    public void register(RegisterUserDto dto) {
        Optional<User> existing = users.findByEmail(dto.getEmail());
        if (existing.isPresent()) {
            throw new RuntimeException("Користувач з таким email вже існує");
        }

        User u = new User();
        u.setEmail(dto.getEmail());
        u.setPassword(passwordEncoder.encode(dto.getPassword()));
        u.setNickname(dto.getNickname());
        u.setRole("REGISTERED");
        u.setVerified(false);

        String token = UUID.randomUUID().toString();
        u.setVerificationToken(token);

        users.save(u);

        // Викликаємо абстрактний порт, а не конкретний MailService
        mailPort.sendVerificationEmail(u.getEmail(), token);
    }

    public boolean verifyAccount(String token) {
        Optional<User> opt = users.findByVerificationToken(token);
        if (opt.isEmpty()) {
            return false;
        }

        User u = opt.get();
        u.setVerified(true);
        u.setRole("USER");
        u.setVerificationToken(null);

        users.save(u);
        return true;
    }

    public User findByEmailOrThrow(String email) {
        return users.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Користувача не знайдено"));
    }
}
