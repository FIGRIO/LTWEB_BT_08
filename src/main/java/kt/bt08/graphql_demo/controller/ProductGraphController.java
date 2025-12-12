package kt.bt08.graphql_demo.controller;

import kt.bt08.graphql_demo.entity.*;
import kt.bt08.graphql_demo.repository.*;
import kt.bt08.graphql_demo.dto.ProductInput; 


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

    // --- QUERY (READ) ---
    @QueryMapping
    public List<Product> getAllProductsSorted() {
        return productRepo.findAll(Sort.by(Sort.Direction.ASC, "price"));
    }

    @QueryMapping
    public List<Product> getProductsByCategory(@Argument Long categoryId) {
        return categoryRepo.findById(categoryId)
                .map(Category::getProducts)
                .orElse(null);
    }

    @QueryMapping
    public List<UserEntity> getAllUsers() {
        return userRepo.findAll();
    }
    
    @QueryMapping
    public List<Category> getAllCategories() {
        return categoryRepo.findAll();
    }

    // --- MUTATION ---

    // 1. Thêm Product dùng Input DTO
    @MutationMapping
    public Product createProduct(@Argument("input") ProductInput input) {
        Product p = new Product();
        p.setTitle(input.title()); 
        p.setPrice(input.price());
        p.setQuantity(input.quantity());
        p.setDescr(input.descr());
        p.setUserid(input.userid());
        
        // Xử lý category
        if(input.categoryId() != null) {
            p.setCategory(categoryRepo.findById(input.categoryId()).orElse(null));
        }
        
        return productRepo.save(p);
    }

    // 2. Các hàm CRUD khác
    @MutationMapping
    public Product updateProduct(@Argument Long id, @Argument String title, @Argument Double price) {
        Product p = productRepo.findById(id).orElse(null);
        if(p != null) {
            if(title != null) p.setTitle(title);
            if(price != null) p.setPrice(price);
            return productRepo.save(p);
        }
        return null;
    }

    @MutationMapping
    public Boolean deleteProduct(@Argument Long id) {
        if(productRepo.existsById(id)) {
            productRepo.deleteById(id);
            return true;
        }
        return false;
    }

    @MutationMapping
    public Category createCategory(@Argument String name, @Argument String images) {
        Category category = new Category();
        category.setName(name);
        category.setImages(images);
        return categoryRepo.save(category);
    }
    
    @MutationMapping
    public Boolean deleteCategory(@Argument Long id) {
        if(categoryRepo.existsById(id)) {
            categoryRepo.deleteById(id);
            return true;
        }
        return false;
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

    @MutationMapping
    public Boolean deleteUser(@Argument Long id) {
        if(userRepo.existsById(id)) {
            userRepo.deleteById(id);
            return true;
        }
        return false;
    }
    
    // Hàm xử lý quan hệ nhiều-nhiều
    @MutationMapping
    public Boolean addUserToCategory(@Argument Long userId, @Argument Long categoryId) {
        UserEntity user = userRepo.findById(userId).orElse(null);
        Category category = categoryRepo.findById(categoryId).orElse(null);

        if (user != null && category != null) {
            user.getCategories().add(category);
            userRepo.save(user);
            return true;
        }
        return false;
    }
}