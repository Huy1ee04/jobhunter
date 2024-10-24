package vn.hungbui.jobhunter.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//dùng để truyền tải dữ liệu phản hồi từ server về client sau khi quá trình đăng nhập thành công.
//Vai trò: Cung cấp accessToken cho client để sử dụng trong các yêu cầu API tiếp theo, giúp xác thực người dùng mà không cần đăng nhập lại.

@Getter
@Setter
public class ResLoginDTO {
    private String accessToken;
    private UserLogin user;

    //Sau khi dùng Lombok thì ko cần làm thủ công như này nữa
//    public String getAccessToken() {
//        return accessToken;
//    }
//    public void setAccessToken(String accessToken) {
//        this.accessToken = accessToken;
//    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    //Khi inner (nested) class(class con) khai báo static thì không cần khai báo object cha(ResLoginDTO) mà có thể sử dụng trực tiếp object con
    public static class UserLogin {
        private long id;
        private String email;
        private String name;
    }
}
