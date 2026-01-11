package sumdu.edu.ua.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import sumdu.edu.ua.core.exceptions.CommentTooOldException;
import sumdu.edu.ua.core.port.CatalogRepositoryPort;
import sumdu.edu.ua.core.port.CommentRepositoryPort;
import sumdu.edu.ua.core.service.CommentService;
import sumdu.edu.ua.core.service.UserService;
import sumdu.edu.ua.web.AppInit;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = AppInit.class)
@AutoConfigureMockMvc(addFilters = false)
class CommentsDeleteExceptionTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentRepositoryPort commentRepo;

    @MockBean
    private CatalogRepositoryPort bookRepo;

    @MockBean
    private UserService userService;

    @MockBean
    private CommentService commentService;

    @Test
    void delete_tooOldComment_returnsBadRequestJson() throws Exception {

        doThrow(new CommentTooOldException(
                "Коментар створено більше ніж 24 години тому і не може бути видалений"
        )).when(commentService)
                .delete(eq(1L), eq(2L), any(Instant.class));

        mockMvc.perform(post("/comments/delete")
                        .param("bookId", "1")
                        .param("commentId", "2")
                        .param("createdAt", "2024-01-01T00:00:00Z"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("Коментар створено більше ніж 24 години тому і не може бути видалений"));
    }
}
