package kt.bt08.graphql_demo.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kt.bt08.graphql_demo.entity.UserEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        UserEntity user = (UserEntity) session.getAttribute("user");
        
        String uri = request.getRequestURI();

        // 1. Nếu chưa đăng nhập -> Đẩy về trang login
        if (user == null) {
            response.sendRedirect("/login");
            return false;
        }

        // 2. Phân quyền ADMIN
        if (uri.startsWith("/admin") && !"ADMIN".equals(user.getRole())) {
            response.sendRedirect("/access-denied"); // Hoặc đẩy về login
            return false;
        }

        // 3. Phân quyền USER
        if (uri.startsWith("/user") && !"USER".equals(user.getRole())) {
            response.sendRedirect("/access-denied");
            return false;
        }

        return true;
    }
}