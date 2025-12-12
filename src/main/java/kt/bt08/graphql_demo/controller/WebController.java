@Controller // Lưu ý: import org.springframework.stereotype.Controller
public class WebController {
    @GetMapping("/")
    public String home() {
        return "index"; // Trả về file index.html
    }
}