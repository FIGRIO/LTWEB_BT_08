package kt.bt08.graphql_demo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    private Integer quantity;
    private String descr;
    private Double price;
    private Long userid;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}