package com.example.graphqldemo.controller;

import com.example.graphqldemo.entity.*;
import com.example.graphqldemo.repository.*;
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
            return cat.getProducts(); // Hibernate sẽ tự fetch list này
        }
        return null;
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
}   