package sumdu.edu.ua.core.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String author;

    @Column(name = "pub_year")
    private Integer pubYear;

    public Book() {
        // JPA requires empty constructor
    }

    public Book(String title, String author, Integer pubYear) {
        this.title = title;
        this.author = author;
        this.pubYear = pubYear;
    }

    // getters and setters

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }

    public void setAuthor(String author) { this.author = author; }

    public Integer getPubYear() { return pubYear; }

    public void setPubYear(Integer pubYear) { this.pubYear = pubYear; }
}
