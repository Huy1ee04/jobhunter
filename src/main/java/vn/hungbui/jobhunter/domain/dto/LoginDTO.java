package vn.hungbui.jobhunter.domain.dto;

import jakarta.validation.constraints.NotBlank;

//Dùng để truyền tải thông tin đăng nhập từ client  đến server khi user yêu cầu đăng nhập.
//Vai trò: Hỗ trợ việc kiểm tra tính hợp lệ của dữ liệu đầu vào tại tầng controller trước khi tiến hành các bước xác thực.
public class LoginDTO {
    @NotBlank(message = "username không được để trống")
    private String username;

    @NotBlank(message = "password không được để trống")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
