package kt.bt08.graphql_demo.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import kt.bt08.graphql_demo.entity.Product;
import kt.bt08.graphql_demo.entity.UserEntity;
import kt.bt08.graphql_demo.repository.ProductRepository;
import kt.bt08.graphql_demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class MvcController {

    @Autowired UserRepository userRepo;
    @Autowired ProductRepository productRepo;

    // --- 1. Xử lý Login ---
    @GetMapping("/login")
    public String showLogin() {
        return "login";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam String email, @RequestParam String password, HttpSession session, Model model) {
        // Tìm user (Demo đơn giản, thực tế cần mã hóa pass)
        UserEntity user = userRepo.findAll().stream()
                .filter(u -> u.getEmail().equals(email) && u.getPassword().equals(password))
                .findFirst().orElse(null);

        if (user != null) {
            session.setAttribute("user", user);
            // Chuyển hướng dựa trên Role
            if ("ADMIN".equals(user.getRole())) {
                return "redirect:/admin/home";
            } else {
                return "redirect:/user/home";
            }
        }
        model.addAttribute("error", "Email hoặc mật khẩu sai!");
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    // --- 2. Trang User ---
    @GetMapping("/user/home")
    public String userHome() {
        return "user_home";
    }

    // --- 3. Trang Admin (Có Form Validator) ---
    @GetMapping("/admin/home")
    public String adminHome(Model model) {
        model.addAttribute("product", new Product()); // Object rỗng để bind vào form
        model.addAttribute("products", productRepo.findAll()); // List sản phẩm hiện có
        return "admin_home";
    }

    @PostMapping("/admin/add-product")
    public String addProduct(@Valid @ModelAttribute("product") Product product, 
                             BindingResult result, 
                             Model model) {
        // Kiểm tra Validator
        if (result.hasErrors()) {
            // Nếu lỗi, trả về trang cũ kèm thông báo lỗi
            model.addAttribute("products", productRepo.findAll());
            return "admin_home";
        }
        // Nếu đúng, lưu vào DB
        productRepo.save(product);
        return "redirect:/admin/home";
    }
    
    @GetMapping("/access-denied")
    @ResponseBody
    public String accessDenied() {
        return "<h1>Bạn không có quyền truy cập trang này!</h1><a href='/login'>Quay lại Login</a>";
    }
}