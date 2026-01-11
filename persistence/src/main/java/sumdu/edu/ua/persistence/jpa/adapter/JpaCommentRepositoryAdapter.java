package sumdu.edu.ua.persistence.jpa.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sumdu.edu.ua.core.domain.Comment;
import sumdu.edu.ua.core.domain.Page;
import sumdu.edu.ua.core.domain.PageRequest;
import sumdu.edu.ua.core.port.CommentRepositoryPort;
import sumdu.edu.ua.persistence.jpa.repo.BookJpaRepository;
import sumdu.edu.ua.persistence.jpa.repo.CommentJpaRepository;

import java.time.Instant;
import java.util.List;

@Repository
//@RequiredArgsConstructor
public class JpaCommentRepositoryAdapter implements CommentRepositoryPort {
    private final CommentJpaRepository repo;

    public JpaCommentRepositoryAdapter(CommentJpaRepository repo) {
        this.repo = repo;
    }


    @Override
    public void add(long bookId, String author, String text) {
        Comment comment = new Comment();
        comment.setBookId(bookId);
        comment.setAuthor(author);
        comment.setText(text);
        comment.setCreatedAt(Instant.now());
        repo.save(comment);
    }

    @Override
    public Page<Comment> list(long bookId, String author, Instant since, PageRequest request) {

        List<Comment> all = repo.findByBookIdOrderByCreatedAtDesc(bookId);

        List<Comment> filtered = all;

        int from = request.getPage() * request.getSize();
        int to = Math.min(from + request.getSize(), filtered.size());
        List<Comment> content = from >= filtered.size() ? List.of() : filtered.subList(from, to);

        int totalPages = (int) Math.ceil((double) filtered.size() / request.getSize());

        return new Page<>(content, request, totalPages);

    }

    @Override
    public void delete(long bookId, long commentId) {
        repo.deleteById(commentId);
    }

    @Override
    public List<Comment> findByAuthor(String author) {
        return repo.findByAuthorOrderByCreatedAtDesc(author);
    }

}
