package sumdu.edu.ua.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.port.CatalogRepositoryPort;
import sumdu.edu.ua.web.mail.MailService;

@Controller
@RequestMapping("/books")
public class BookFormController {
    private final MailService mailService;
    private final CatalogRepositoryPort bookRepo;

    public BookFormController(CatalogRepositoryPort bookRepo, MailService mailService) {
        this.bookRepo = bookRepo;
        this.mailService = mailService;
    }

    @GetMapping("/new")
    public String showAddForm(Model model) {
        model.addAttribute("book", new Book());
        return "book-form";
    }

    @PostMapping
    public String addBook(@ModelAttribute Book book) {

        if (book.getTitle() != null && !book.getTitle().isBlank()) {
            bookRepo.add(book.getTitle(), book.getAuthor(), book.getPubYear());
        }
        mailService.sendNewBookEmail(book);
        return "redirect:/books";
    }
}
