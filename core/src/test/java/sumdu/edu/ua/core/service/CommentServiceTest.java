package sumdu.edu.ua.core.service;

import org.junit.jupiter.api.Test;
import sumdu.edu.ua.core.exceptions.CommentTooOldException;
import sumdu.edu.ua.core.exceptions.InvalidCommentDeleteException;
import sumdu.edu.ua.core.port.CommentRepositoryPort;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class CommentServiceTest {

    @Test
    void delete_withNegativeIds_throwsInvalidCommentDeleteException() {
        CommentRepositoryPort repo = mock(CommentRepositoryPort.class);
        CommentService service = new CommentService(repo);

        assertThrows(InvalidCommentDeleteException.class,
                () -> service.delete(-1, 0, Instant.now()));

        verifyNoInteractions(repo);
    }

    @Test
    void delete_withoutCreatedAt_throwsInvalidCommentDeleteException() {
        CommentRepositoryPort repo = mock(CommentRepositoryPort.class);
        CommentService service = new CommentService(repo);

        assertThrows(InvalidCommentDeleteException.class,
                () -> service.delete(1, 1, null));

        verifyNoInteractions(repo);
    }

    @Test
    void delete_tooOldComment_throwsCommentTooOldException() {
        CommentRepositoryPort repo = mock(CommentRepositoryPort.class);
        CommentService service = new CommentService(repo);

        Instant twoDaysAgo = Instant.now().minusSeconds(48 * 3600);

        assertThrows(CommentTooOldException.class,
                () -> service.delete(1, 1, twoDaysAgo));

        verifyNoInteractions(repo);
    }

    @Test
    void delete_validData_callsRepositoryDelete() {
        CommentRepositoryPort repo = mock(CommentRepositoryPort.class);
        CommentService service = new CommentService(repo);

        Instant oneHourAgo = Instant.now().minusSeconds(3600);

        service.delete(10, 20, oneHourAgo);

        verify(repo).delete(10, 20);
    }
}
