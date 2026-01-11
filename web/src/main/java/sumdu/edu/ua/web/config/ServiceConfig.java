package sumdu.edu.ua.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sumdu.edu.ua.core.port.CommentRepositoryPort;
import sumdu.edu.ua.core.service.CommentService;

@Configuration
public class ServiceConfig {

    @Bean
    public CommentService commentService(CommentRepositoryPort commentRepositoryPort) {
        return new CommentService(commentRepositoryPort);
    }
}
