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
import org.springframework.web.multipart.MultipartFile; // Import xử lý file
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // Import Flash Message

import java.io.InputStream;
import java.nio.file.*;
import java.io.IOException;

// Nếu bạn muốn dùng BCrypt (đã tải thư viện), hãy import:
// import org.mindrot.jbcrypt.BCrypt; 

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
    public String doLogin(@RequestParam String email, 
                          @RequestParam String password, 
                          HttpSession session, 
                          Model model) {
        
        // SỬA LỖI: Gán trực tiếp kết quả tìm kiếm vào biến user
        // (Tránh lỗi variable not initialized)
        UserEntity user = userRepo.findAll().stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst()
                .orElse(null);

        // Kiểm tra đăng nhập
        // LƯU Ý: Vì Database đang lưu "123456" (plain text), ta dùng .equals()
        // Nếu bạn đã mã hóa password trong DB, hãy dùng: if (user != null && BCrypt.checkpw(password, user.getPassword()))
        if (user != null && user.getPassword().equals(password)) {
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
    public String userHome(Model model) {
        model.addAttribute("products", productRepo.findAll());
        return "user_home";
    }

    // --- 3. Trang Admin (Có Validator + Upload + Flash Message) ---
    @GetMapping("/admin/home")
    public String adminHome(Model model) {
        if (!model.containsAttribute("product")) {
            model.addAttribute("product", new Product());
        }
        model.addAttribute("products", productRepo.findAll());
        return "admin_home";
    }

    @PostMapping("/admin/add-product")
    public String addProduct(@Valid @ModelAttribute("product") Product product,
                             BindingResult result,
                             @RequestParam("imageFile") MultipartFile imageFile, // Hứng file ảnh
                             RedirectAttributes redirectAttributes, // Hứng thông báo
                             Model model) {
        
        // 1. Validation lỗi nhập liệu
        if (result.hasErrors()) {
            model.addAttribute("products", productRepo.findAll());
            return "admin_home"; // Trả về trang cũ để hiện lỗi
        }

        // 2. Xử lý Upload Ảnh
        if (!imageFile.isEmpty()) {
            try {
                String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
                Path uploadPath = Paths.get("uploads");
                
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                try (InputStream inputStream = imageFile.getInputStream()) {
                    Files.copy(inputStream, uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
                }

                product.setImage(fileName); // Lưu tên ảnh vào DB

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 3. Lưu xuống DB
        productRepo.save(product);
        
        // 4. Tạo thông báo thành công (Flash Message)
        redirectAttributes.addFlashAttribute("successMessage", "Đã thêm sản phẩm: " + product.getTitle());
        
        return "redirect:/admin/home";
    }
    
    @GetMapping("/access-denied")
    @ResponseBody
    public String accessDenied() {
        return "<h1>Bạn không có quyền truy cập trang này!</h1><a href='/login'>Quay lại Login</a>";
    }

    @GetMapping("/admin/delete/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            // Kiểm tra xem sản phẩm có tồn tại không
            if (productRepo.existsById(id)) {
                productRepo.deleteById(id);
                redirectAttributes.addFlashAttribute("successMessage", "Đã xóa sản phẩm thành công!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy sản phẩm!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: Không thể xóa sản phẩm này (có thể do ràng buộc dữ liệu).");
        }
        return "redirect:/admin/home";
    }

    @GetMapping("/admin/edit/{id}")
    public String editProduct(@PathVariable Long id, Model model) {
        Product p = productRepo.findById(id).orElse(null);
        if (p == null) {
            return "redirect:/admin/home"; // Không tìm thấy thì quay về
        }
        
        // Đưa sản phẩm tìm được vào model để Form tự động điền dữ liệu
        model.addAttribute("product", p); 
        
        // Vẫn cần load danh sách để hiển thị bên phải
        model.addAttribute("products", productRepo.findAll());
        
        return "admin_home";
    }
    @GetMapping("/")
    public String landing(HttpSession session) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        
        // 1. Chưa đăng nhập -> Về trang login
        if (user == null) {
            return "redirect:/login";
        }
        
        // 2. Đã đăng nhập -> Kiểm tra quyền để điều hướng
        if ("ADMIN".equals(user.getRole())) {
            return "redirect:/admin/home";
        } else {
            return "redirect:/user/home";
        }
    }
}