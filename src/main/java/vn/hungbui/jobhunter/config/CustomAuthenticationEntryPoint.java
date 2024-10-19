package vn.hungbui.jobhunter.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import vn.hungbui.jobhunter.domain.RestResponse;

import java.io.IOException;
import java.util.Optional;

//Lớp CustomAuthenticationEntryPoint này tùy chỉnh cách mà Spring Security xử lý các lỗi xác thực khi JWT không hợp lệ.
//Lỗi này xảy ra ở tầng filter (các lỗi authen và author là lỗi ở tầng filter)

//interface AuthenticationEntryPoint Xử lý các yêu cầu không được xác thực (do thiếu token, token sai,...)
// Và interface này có 1 phương thức là commence()
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final AuthenticationEntryPoint delegate = new BearerTokenAuthenticationEntryPoint();

    private final ObjectMapper mapper;

    public CustomAuthenticationEntryPoint(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    // Viết lại phương thức commence để trả về định dạng phản hồi lỗi mong muốn
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        this.delegate.commence(request, response, authException);
        response.setContentType("application/json;charset=UTF-8");

        RestResponse<Object> res = new RestResponse<Object>();
        res.setStatusCode(HttpStatus.UNAUTHORIZED.value());

        String errorMessage = Optional.ofNullable(authException.getCause())
                .map(Throwable::getMessage) //nếu có cause thì trả ra authException().getCause().getMessage().
                .orElse(authException.getMessage()); // nếu k có cause thì lấy trực tiếp authException().getMessage()
        res.setError(errorMessage);

        res.setMessage("Token không hợp lệ (hết hạn, không đúng định dạng, hoặc không truyền JWT ở header)...");

        mapper.writeValue(response.getWriter(), res);
    }
}