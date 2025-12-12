package kt.bt08.graphql_demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String images;

    // Quan hệ 1-Nhiều với Product
    @OneToMany(mappedBy = "category")
    private List<Product> products;
    
    // Quan hệ Nhiều-Nhiều với User
    @ManyToMany(mappedBy = "categories")
    private List<UserEntity> users;
}