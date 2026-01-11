package sumdu.edu.ua.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sumdu.edu.ua.core.exceptions.CommentTooOldException;
import sumdu.edu.ua.core.exceptions.InvalidCommentDeleteException;
import sumdu.edu.ua.core.port.CommentRepositoryPort;

import java.time.Duration;
import java.time.Instant;

public class CommentService {

    private static final Logger log = LoggerFactory.getLogger(CommentService.class);

    private final CommentRepositoryPort repo;

    public CommentService(CommentRepositoryPort repo) {
        this.repo = repo;
    }

    /**
     * Видалення коментаря до книги.
     *
     * @param bookId    ID книги
     * @param commentId ID коментаря
     * @param createdAt час створення коментаря
     */
    public void delete(long bookId, long commentId, Instant createdAt) {

        // 1) Базова перевірка ідентифікаторів
        if (bookId <= 0 || commentId <= 0) {
            log.warn("Invalid delete request: bookId={}, commentId={}", bookId, commentId);
            throw new InvalidCommentDeleteException("Некоректний ідентифікатор книги або коментаря");
        }

        // 2) Перевірка наявності createdAt
        if (createdAt == null) {
            log.warn("Delete request without createdAt for bookId={}, commentId={}", bookId, commentId);
            throw new InvalidCommentDeleteException("Відсутня інформація про час створення коментаря");
        }

        // 3) Перевірка віку коментаря
        long hours = Duration.between(createdAt, Instant.now()).toHours();
        if (hours > 24) {
            log.warn("Attempt to delete too old comment: bookId={}, commentId={}, age={}h",
                    bookId, commentId, hours);
            throw new CommentTooOldException("Коментар створено більше ніж 24 години тому і не може бути видалений");
        }

        // 4) Все ок — видаляємо
        log.info("Deleting comment: bookId={}, commentId={}", bookId, commentId);
        repo.delete(bookId, commentId);
    }
}
