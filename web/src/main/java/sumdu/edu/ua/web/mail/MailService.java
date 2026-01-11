package sumdu.edu.ua.web.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.port.MailPort;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class MailService implements MailPort {

    private static final Logger log = LoggerFactory.getLogger(MailService.class);
    private static final String RESEND_API_URL = "https://api.resend.com/emails";

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${app.mail.admin}")
    private String adminEmail;

    @Value("${mail.resend.api-key}")
    private String apiKey;

    @Value("${mail.resend.from}")
    private String fromAddress;

    private final EmailTemplateProcessor templateProcessor;
    private final RestTemplate restTemplate = new RestTemplate();

    public MailService(EmailTemplateProcessor templateProcessor) {
        this.templateProcessor = templateProcessor;
    }

    @Override
    public void sendVerificationEmail(String email, String token) {
        Map<String, Object> model = new HashMap<>();

        String confirmUrl = baseUrl + "/confirm?token=" + token;
        model.put("confirmUrl", confirmUrl);
        model.put("email", email);

        String html = templateProcessor.processTemplate("verify.ftl", model);

        sendHtml(email, "Підтвердження акаунту", html);
    }

    public void sendNewBookEmail(Book book) {
        Map<String, Object> model = new HashMap<>();
        model.put("appBaseUrl", baseUrl);
        model.put("id", book.getId());
        model.put("title", book.getTitle());
        model.put("author", book.getAuthor());
        model.put("year", book.getPubYear());
        model.put("comments", null);
        model.put(
                "createdAt",
                Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant())
        );

        String html = templateProcessor.processTemplate("new_book.ftl", model);

        sendHtml(adminEmail, "Нова книга в каталозі", html);
    }

    private void sendHtml(String to, String subject, String html) {
        if (apiKey == null || apiKey.isBlank()) {
            log.error("Resend API key is not configured. Cannot send email '{}'", subject);
            return;
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            Map<String, Object> body = new HashMap<>();
            body.put("from", fromAddress);
            body.put("to", List.of(to));
            body.put("subject", subject);
            body.put("html", html);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            ResponseEntity<String> response =
                    restTemplate.postForEntity(RESEND_API_URL, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Email sent to {} with subject '{}'", to, subject);
            } else {
                log.error("Failed to send email. Status: {}, body: {}",
                        response.getStatusCode().value(), response.getBody());
                throw new RuntimeException("Resend API error, status " +
                        response.getStatusCode().value());
            }
        } catch (Exception e) {
            log.error("Failed to send email to {}", to, e);
            throw new RuntimeException("Cannot send email", e);
        }
    }
}
