package sumdu.edu.ua.persistence.jpa.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import sumdu.edu.ua.core.domain.Comment;

import java.util.List;

public interface CommentJpaRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByBookIdOrderByCreatedAtDesc(Long bookId);
    List<Comment> findByAuthorOrderByCreatedAtDesc(String author);

}
