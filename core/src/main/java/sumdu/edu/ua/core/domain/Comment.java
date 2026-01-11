package sumdu.edu.ua.core.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long bookId;

    private String author;

    @Column(length = 2000)
    private String text;

    private Instant createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Comment() {
        // required by JPA
    }

    // optional: конструктор для створення через сервіс, але БЕЗ id
    public Comment(Long bookId, String author, String text, Instant createdAt) {
        this.bookId = bookId;
        this.author = author;
        this.text = text;
        this.createdAt = createdAt;
    }

    // getters
    public Long getId() { return id; }
    public Long getBookId() { return bookId; }
    public String getAuthor() { return author; }
    public String getText() { return text; }
    public Instant getCreatedAt() { return createdAt; }

    // setters (private/protected — але можна й public)
    public void setId(Long id) { this.id = id; }
    public void setBookId(Long bookId) { this.bookId = bookId; }
    public void setAuthor(String author) { this.author = author; }
    public void setText(String text) { this.text = text; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
