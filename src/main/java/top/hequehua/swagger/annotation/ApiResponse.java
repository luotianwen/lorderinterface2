package top.hequehua.swagger.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 接口返回参数（可多个不同返回）
 *
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiResponse {
    /**
     * http响应状态码
     *
     * @return
     */
    String code() default "200";

    /**
     * 描述
     *
     * @return
     */
    String message() default "OK";

    /**
     * 返回类型
     *
     * @return
     */
    Class<?> response() default Void.class;

}
