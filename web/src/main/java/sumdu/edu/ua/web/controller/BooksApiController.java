package sumdu.edu.ua.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.domain.Page;
import sumdu.edu.ua.core.domain.PageRequest;
import sumdu.edu.ua.core.port.CatalogRepositoryPort;


@RestController
@RequestMapping("/api/books")
public class BooksApiController {

    private static final Logger log = LoggerFactory.getLogger(BooksApiController.class);

    private final CatalogRepositoryPort bookRepo;

    public BooksApiController(CatalogRepositoryPort bookRepo) {
        this.bookRepo = bookRepo;
    }


    @GetMapping
    public ResponseEntity<Page<Book>> getBooks(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            Page<Book> result = bookRepo.search(q, new PageRequest(page, size));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Помилка доступу до БД при виконанні GET /api/books", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<?> addBook(@RequestBody Book book) {
        try {
            if (book.getTitle() == null || book.getTitle().isBlank()
                    || book.getAuthor() == null || book.getAuthor().isBlank()) {
                return ResponseEntity.badRequest().body("title & author required");
            }
            if (book.getPubYear() <= 0) {
                return ResponseEntity.badRequest().body("invalid pubYear");
            }

            Book saved = bookRepo.add(
                    book.getTitle().trim(),
                    book.getAuthor().trim(),
                    book.getPubYear()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(saved);

        } catch (Exception e) {
            log.error("Помилка доступу до БД при виконанні POST /api/books", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("DB error");
        }
    }
}
