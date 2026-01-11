package sumdu.edu.ua.web.helpers;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sumdu.edu.ua.core.domain.User;
import sumdu.edu.ua.core.service.UserService;

@Service
public class AppUserDetailsService implements UserDetailsService {

    private final UserService userService;

    public AppUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User u;
        try {
            u = userService.findByEmailOrThrow(email);
        } catch (RuntimeException ex) {
            throw new UsernameNotFoundException("Користувача не знайдено");
        }

        String role = (u.getRole() == null || u.getRole().isBlank()) ? "USER" : u.getRole();

        return org.springframework.security.core.userdetails.User
                .withUsername(u.getEmail())
                .password(u.getPassword())
                .roles(role)
                .disabled(!u.isVerified())
                .build();
    }
}
