package sumdu.edu.ua.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import sumdu.edu.ua.web.helpers.AppUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AppUserDetailsService userDetailsService;

    public SecurityConfig(AppUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(auth -> auth

                        // NEW: публічний health-check для Render / моніторингу
                        .requestMatchers("/health").permitAll()

                        // NEW: усі Actuator-ендпоінти — тільки для ADMIN
                        .requestMatchers("/actuator/**").hasRole("ADMIN")

                        // статика
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()

                        // публічні сторінки
                        .requestMatchers("/login", "/register", "/confirm").permitAll()

                        // додавання / видалення коментарів — тільки залогінені
                        .requestMatchers(HttpMethod.POST, "/comments/**")
                        .hasAnyRole("USER", "MODERATOR")

                        // форма + POST для книг — тільки MODERATOR
                        .requestMatchers("/books/new").hasRole("MODERATOR")
                        .requestMatchers(HttpMethod.POST, "/books/**").hasRole("MODERATOR")

                        // все інше — достатньо бути залогіненим (USER або MODERATOR або ADMIN)
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                .userDetailsService(userDetailsService);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
