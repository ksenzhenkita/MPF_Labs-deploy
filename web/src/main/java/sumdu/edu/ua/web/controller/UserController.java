package sumdu.edu.ua.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import sumdu.edu.ua.core.domain.Comment;
import sumdu.edu.ua.core.port.CommentRepositoryPort;

import java.util.List;

@Controller
public class UserController {
    private final CommentRepositoryPort commentRepo;

    public UserController(CommentRepositoryPort commentRepo) {
        this.commentRepo = commentRepo;
    }


    @GetMapping("/user/{name}")
    public String userComments(@PathVariable("name") String name, Model model) {

        List<Comment> comments = commentRepo.findByAuthor(name);

        model.addAttribute("username", name);
        model.addAttribute("comments", comments);

        return "user_comments";
    }
}
