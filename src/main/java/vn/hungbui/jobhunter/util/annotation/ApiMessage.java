package vn.hungbui.jobhunter.util.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) //Chỉ định rằng annotation ApiMessage sẽ được giữ lại tại thời điểm runtime.
                                    // Điều này có nghĩa là annotation này có thể được truy cập thông qua reflection khi chương trình đang chạy.
@Target(ElementType.METHOD)
public @interface ApiMessage {
    String value();
}
