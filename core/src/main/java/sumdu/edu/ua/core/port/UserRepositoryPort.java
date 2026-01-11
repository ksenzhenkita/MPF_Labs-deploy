package sumdu.edu.ua.core.port;

import sumdu.edu.ua.core.domain.User;

import java.util.Optional;

public interface UserRepositoryPort {

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    User save(User user);

    Optional<User> findByVerificationToken(String token);
}
