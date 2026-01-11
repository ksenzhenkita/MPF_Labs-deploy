package sumdu.edu.ua.persistence.jpa.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.domain.Page;
import sumdu.edu.ua.core.domain.PageRequest;
import sumdu.edu.ua.core.port.CatalogRepositoryPort;
import sumdu.edu.ua.persistence.jpa.repo.BookJpaRepository;

@Repository
//@RequiredArgsConstructor
public class JpaCatalogRepositoryAdapter implements CatalogRepositoryPort {

    private final BookJpaRepository repo;

    public JpaCatalogRepositoryAdapter(BookJpaRepository repo) {
        this.repo = repo;
    }

    @Override
    public Page<Book> search(String query, PageRequest request) {

        var pageable = org.springframework.data.domain.PageRequest.of(
                request.getPage(),
                request.getSize()
        );

        var page = repo.findAll(pageable);

        return new Page<>(
                page.getContent(),
                request,
                page.getTotalElements()
        );

    }

    @Override
    public Book findById(long id) {
        return repo.findById(id).orElse(null);
    }

    @Override
    public Book add(String title, String author, int pubYear) {

        // створення JPA сутності
        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setPubYear(pubYear);

        // JPA робить INSERT
        return repo.save(book);
    }
}
