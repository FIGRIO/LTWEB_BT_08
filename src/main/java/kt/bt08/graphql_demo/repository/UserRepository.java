package kt.bt08.graphql_demo.repository;

import kt.bt08.graphql_demo.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> { }