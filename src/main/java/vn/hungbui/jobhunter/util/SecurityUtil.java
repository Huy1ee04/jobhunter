package vn.hungbui.jobhunter.util;

import com.nimbusds.jose.util.Base64;
import com.nimbusds.jwt.JWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import vn.hungbui.jobhunter.domain.dto.ResLoginDTO;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    private long accessTokenExpiration;

    @Value("${hungbui.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    //phương thức tạo JWT dựa trên đối tượng Authentication cung cấp
    //Video 88: thay tham số Authentication authentication thành String email
    public String createAccessToken(String email, ResLoginDTO.UserLogin resLoginDTO) {
        Instant now = Instant.now();
        //Lấy thời gian hiện tại (now) và tính thời gian hết hạn bằng cách cộng thêm giá trị accessTokenExpiration.
        Instant validity = now.plus(this.accessTokenExpiration, ChronoUnit.SECONDS);

        // hardcode permission (for testing)
        List<String> listAuthority = new ArrayList<String>();
        listAuthority.add("ROLE_USER_CREATE");
        listAuthority.add("ROLE_USER_UPDATE");

        // Định nghĩa các claims (payload) của JWT
        // @formatter:off
        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuedAt(now) //Thiết lập thời gian cấp token.
            .expiresAt(validity) //Thiết lập tg hết hạn
            //.subject(authentication.getName())
                .subject(email) //Video 88
            //.claim("hungbui", authentication)  nếu payload trả về authentication thì phần payload sẽ chứa nhiều thông tin không cần thiết
            .claim("user",resLoginDTO) //Trả về resLoginDTO.getUser() là đủ
            .claim("permission", listAuthority)
            .build();

        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();

    }

    public String createRefreshToken(String email, ResLoginDTO dto) {
        Instant now = Instant.now();
        //Lấy thời gian hiện tại (now) và tính thời gian hết hạn bằng cách cộng thêm giá trị accessTokenExpiration.
        Instant validity = now.plus(this.refreshTokenExpiration, ChronoUnit.SECONDS);
        // @formatter:off
        // Định nghĩa các claims (payload) của JWT
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now) //Thiết lập thời gian cấp token.
                .expiresAt(validity) //Thiết lập tg hết hạn
                .subject(email)
                .claim("user", dto.getUser())  //Thêm một claim tùy chỉnh vào JWT. Ở đây, khóa của claim là "hungbui", giá trị là đối tượng Authentication.
                .build();

        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();

    }

    //Video 87,88: check Valid refresh token
    private SecretKey getSecretKey() {
        byte[] keyBytes = Base64.from(jwtKey).decode();
        return new SecretKeySpec(keyBytes, 0, keyBytes.length,
                JWT_ALGORITHM.getName());
    }
    public Jwt checkValidRefreshToken(String token){
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(
                getSecretKey()).macAlgorithm(SecurityUtil.JWT_ALGORITHM).build();
        try {
            return jwtDecoder.decode(token);
        } catch (Exception e) {
            System.out.println(">>> Refresh Token error: " + e.getMessage());
            throw e;
        }
    }
    /**
     * Get the login of the current user.
     *
     * @return the login of the current user.
     */
    // Lấy thông tin đăng nhập của người dùng hiện tại (được gọi trong entity Company)
    public static Optional<String> getCurrentUserLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(extractPrincipal(securityContext.getAuthentication()));
    }

    //lấy tên người dùng từ Authentication, getCurrentUserLogin sẽ dùng phương thức này.
    private static String extractPrincipal(Authentication authentication) {
        if (authentication == null) {
            return null;
        } else if (authentication.getPrincipal() instanceof UserDetails springSecurityUser) {
            return springSecurityUser.getUsername();
        } else if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getSubject();
        } else if (authentication.getPrincipal() instanceof String s) {
            return s;
        }
        return null;
    }

    /**
     * Get the JWT of the current user.
     *
     * @return the JWT of the current user.
     */
    public static Optional<String> getCurrentUserJWT() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(securityContext.getAuthentication())
                .filter(authentication -> authentication.getCredentials() instanceof String)
                .map(authentication -> (String) authentication.getCredentials());
    }

    /**
     * Check if a user is authenticated.
     *
     * @return true if the user is authenticated, false otherwise.
     */
    // public static boolean isAuthenticated() {
    //     Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    //     return authentication != null && getAuthorities(authentication).noneMatch(AuthoritiesConstants.ANONYMOUS::equals);
    // }

    /**
     * Checks if the current user has any of the authorities.
     *
     * @param authorities the authorities to check.
     * @return true if the current user has any of the authorities, false otherwise.
     */
    // public static boolean hasCurrentUserAnyOfAuthorities(String... authorities) {
    //     Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    //     return (
    //         authentication != null && getAuthorities(authentication).anyMatch(authority -> Arrays.asList(authorities).contains(authority))
    //     );
    // }

    /**
     * Checks if the current user has none of the authorities.
     *
     * @param authorities the authorities to check.
     * @return true if the current user has none of the authorities, false otherwise.
     */
    // public static boolean hasCurrentUserNoneOfAuthorities(String... authorities) {
    //     return !hasCurrentUserAnyOfAuthorities(authorities);
    // }

    /**
     * Checks if the current user has a specific authority.
     *
     * @param authority the authority to check.
     * @return true if the current user has the authority, false otherwise.
     */
    // public static boolean hasCurrentUserThisAuthority(String authority) {
    //     return hasCurrentUserAnyOfAuthorities(authority);
    // }

    // private static Stream<String> getAuthorities(Authentication authentication) {
    //     return authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority);
    // }

}


