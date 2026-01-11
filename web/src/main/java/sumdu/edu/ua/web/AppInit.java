package sumdu.edu.ua.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication(scanBasePackages = "sumdu.edu.ua")
@EnableJpaRepositories(basePackages = "sumdu.edu.ua.persistence.jpa.repo")
@EntityScan(basePackages = "sumdu.edu.ua.core.domain")
@ComponentScan(basePackages = "sumdu.edu.ua")
public class AppInit {
    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(AppInit.class, args);
    }
}


