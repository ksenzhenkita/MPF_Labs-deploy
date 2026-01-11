package sumdu.edu.ua.persistence.jpa.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sumdu.edu.ua.core.domain.Book;

@Repository
public interface BookJpaRepository extends JpaRepository<Book, Long> {

}
