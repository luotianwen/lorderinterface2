package top.hequehua.swagger.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Controller描述
 *
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Api {
    /**
     * url的路径值
     *
     * @return
     */
    String value() default "";

    /**
     * 如果设置这个值、value的值会被覆盖
     *
     * @return
     */
    String[] tags() default {};

    /**
     * 接口描述
     *
     * @return
     */
    String description() default "";

    /**
     * 是否显示
     */
    boolean hidden() default false;
}
