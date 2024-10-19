package vn.hungbui.jobhunter.domain.dto;

//dùng để truyền tải dữ liệu phản hồi từ server về client sau khi quá trình đăng nhập thành công.
//Vai trò: Cung cấp accessToken cho client để sử dụng trong các yêu cầu API tiếp theo, giúp xác thực người dùng mà không cần đăng nhập lại.
public class ResLoginDTO {
    private String accessToken;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

}
