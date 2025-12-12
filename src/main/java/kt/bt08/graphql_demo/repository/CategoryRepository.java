package kt.bt08.graphql_demo.repository;
import kt.bt08.graphql_demo.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> { }