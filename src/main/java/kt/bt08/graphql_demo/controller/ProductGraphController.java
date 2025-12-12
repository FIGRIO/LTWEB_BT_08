package kt.bt08.graphql_demo.controller;

import kt.bt08.graphql_demo.entity.*;
import kt.bt08.graphql_demo.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class ProductGraphController {

    @Autowired private ProductRepository productRepo;
    @Autowired private CategoryRepository categoryRepo;
    @Autowired private UserRepository userRepo;

    // 1. Hiển thị tất cả product có price từ thấp đến cao
    @QueryMapping
    public List<Product> getAllProductsSorted() {
        return productRepo.findAll(Sort.by(Sort.Direction.ASC, "price"));
    }

    // 2. Lấy tất cả product của 01 category
    @QueryMapping
    public List<Product> getProductsByCategory(@Argument Long categoryId) {
        Category cat = categoryRepo.findById(categoryId).orElse(null);
        if (cat != null) {
            return cat.getProducts();
        }
        return null;
    }
    @QueryMapping
    public List<UserEntity> getAllUsers() {
        return userRepo.findAll();
    }

    @MutationMapping
    public UserEntity createUser(@Argument String fullname, @Argument String email, @Argument String phone) {
        UserEntity user = new UserEntity();
        user.setFullname(fullname);
        user.setEmail(email);
        user.setPhone(phone);
        user.setPassword("123456"); 
        return userRepo.save(user);
}

    // 3. CRUD: Ví dụ thêm Product
    @MutationMapping
    public Product createProduct(@Argument String title, @Argument Double price, @Argument Long categoryId) {
        Product p = new Product();
        p.setTitle(title);
        p.setPrice(price);
        p.setCategory(categoryRepo.findById(categoryId).orElse(null));
        return productRepo.save(p);
    }

    @MutationMapping
    public Category createCategory(@Argument String name, @Argument String images) {
        Category category = new Category();
        category.setName(name);
        category.setImages(images);
        return categoryRepo.save(category);
    }
    // Thêm vào ProductGraphController.java

    @MutationMapping
    public Boolean deleteProduct(@Argument Long id) {
        if(productRepo.existsById(id)) {
            productRepo.deleteById(id);
            return true;
        }
        return false;
    }
}