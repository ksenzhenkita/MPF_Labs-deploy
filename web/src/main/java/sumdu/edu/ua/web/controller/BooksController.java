package sumdu.edu.ua.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.domain.Page;
import sumdu.edu.ua.core.domain.PageRequest;
import sumdu.edu.ua.core.port.CatalogRepositoryPort;

@Controller
@RequestMapping("/books")
public class BooksController {

    private final CatalogRepositoryPort bookRepo;

    public BooksController(CatalogRepositoryPort bookRepo) {
        this.bookRepo = bookRepo;
    }

    @GetMapping
    public String getAllBooks(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model
    ) {
        if (page < 0) page = 0;
        if (size < 1) size = 20;
        if (size > 100) size = 100;

        Page<Book> result = bookRepo.search(q, new PageRequest(page, size));

        long total = result.getTotal();
        int totalPages = (int) ((total + size - 1) / size); // ceil(total/size)

        model.addAttribute("books", result.getItems());
        model.addAttribute("q", q);
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("total", total);
        model.addAttribute("totalPages", totalPages);

        model.addAttribute("hasPrev", page > 0);
        model.addAttribute("hasNext", page + 1 < totalPages);

        return "books";
    }

    @GetMapping("/{id}")
    public String getBookById(@PathVariable long id) {
        return "redirect:/comments?bookId=" + id;
    }
}
