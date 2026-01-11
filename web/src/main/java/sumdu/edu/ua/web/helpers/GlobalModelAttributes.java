package sumdu.edu.ua.web.helpers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import sumdu.edu.ua.core.domain.User;
import sumdu.edu.ua.core.service.UserService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.stream.Collectors;

@ControllerAdvice
@Component
public class GlobalModelAttributes {


    private final UserService userService;
    private final DateTimeFormatter timeFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public GlobalModelAttributes(UserService userService) {
        this.userService = userService;
    }

    @ModelAttribute("uri")
    public String uri(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String query = request.getQueryString();

        if (query == null || query.isEmpty()) {
            return uri;
        }

        // видаляємо всі lang=...
        String cleaned = Arrays.stream(query.split("&"))
                .filter(param -> !param.startsWith("lang="))
                .collect(Collectors.joining("&"));

        return cleaned.isEmpty()
                ? uri
                : uri + "?" + cleaned;
    }


    @ModelAttribute("currentUserNickname")
    public String currentUserNickname() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null
                || !auth.isAuthenticated()
                || auth instanceof AnonymousAuthenticationToken) {
            return null;
        }

        String email = auth.getName();
        try {
            User u = userService.findByEmailOrThrow(email);
            return u.getNickname();
        } catch (RuntimeException ex) {
            return null;
        }
    }

    @ModelAttribute("currentTime")
    public String currentTime() {
        return LocalDateTime.now().format(timeFormatter);
    }
}
