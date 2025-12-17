package kt.bt08.graphql_demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*; // Import mới
import lombok.Data;

@Entity
@Data
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Thêm validation: Không để trống
    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String title;

    private Integer quantity;
    private String descr;

    // Thêm validation: Phải là số dương
    @NotNull(message = "Giá không được để trống")
    @Min(value = 0, message = "Giá sản phẩm phải lớn hơn 0")
    private Double price;
    
    private Long userid;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}