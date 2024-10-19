package vn.hungbui.jobhunter.util;

import com.nimbusds.jwt.JWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

//Lớp SecurityUtil được thiết kế để tạo các JWT dựa trên đối tượng Authentication cung cấp.
@Service

public class SecurityUtil {

    private final JwtEncoder jwtEncoder;

    public SecurityUtil(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    public static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS512;

    @Value("${hungbui.jwt.base64-secret}")
    private String jwtKey;

    @Value("${hungbui.jwt.access-token-validity-in-seconds}")
    private long jwtExpiration;

    //phương thức tạo JWT dựa trên đối tượng Authentication cung cấp
    public String createToken(Authentication authentication) {
        Instant now = Instant.now();
        //Lấy thời gian hiện tại (now) và tính thời gian hết hạn bằng cách cộng thêm giá trị jwtExpiration.
        Instant validity = now.plus(this.jwtExpiration, ChronoUnit.SECONDS);
        // @formatter:off
        // Định nghĩa các claims (payload) của JWT
        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuedAt(now) //Thiết lập thời gian cấp token.
            .expiresAt(validity) //Thiết lập tg hết hạn
            .subject(authentication.getName())
            .claim("hungbui", authentication)  //Thêm một claim tùy chỉnh vào JWT. Ở đây, khóa của claim là "hungbui", giá trị là đối tượng Authentication.
            .build();

        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();

    }
}
