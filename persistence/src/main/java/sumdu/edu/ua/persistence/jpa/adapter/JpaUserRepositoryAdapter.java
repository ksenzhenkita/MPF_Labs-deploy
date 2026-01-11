package sumdu.edu.ua.persistence.jpa.adapter;

import org.springframework.stereotype.Repository;
import sumdu.edu.ua.core.domain.User;
import sumdu.edu.ua.core.port.UserRepositoryPort;
import sumdu.edu.ua.persistence.jpa.repo.UserJpaRepository;

import java.util.Optional;

@Repository
public class JpaUserRepositoryAdapter implements UserRepositoryPort {

    private final UserJpaRepository jpa;

    public JpaUserRepositoryAdapter(UserJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<User> findById(Long id) {
        return jpa.findById(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpa.findByEmail(email);
    }

    @Override
    public Optional<User> findByVerificationToken(String token) {
        return jpa.findByVerificationToken(token);
    }

    @Override
    public User save(User user) {
        return jpa.save(user);
    }
}
