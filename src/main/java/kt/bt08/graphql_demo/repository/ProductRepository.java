package kt.bt08.graphql_demo.repository;
import kt.bt08.graphql_demo.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> { }