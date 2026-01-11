package sumdu.edu.ua.web.mail;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;


@org.springframework.context.annotation.Configuration
public class EmailFreemarkerConfig {

    @Bean(name = "emailFreemarkerConfiguration")
    public Configuration emailFreemarkerConfiguration() {

        Configuration cfg = new Configuration(Configuration.VERSION_2_3_32);

        cfg.setDefaultEncoding("UTF-8");
        cfg.setClassLoaderForTemplateLoading(
                getClass().getClassLoader(),
                "templates/email"
        );
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        return cfg;
    }
}

