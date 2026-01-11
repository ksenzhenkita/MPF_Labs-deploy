package sumdu.edu.ua.core.port;

public interface MailPort {

    void sendVerificationEmail(String email, String token);

    // Якщо захочеш винести й нотифікацію про нову книгу в цей порт:
    // void sendNewBookEmail(Book book);
}
