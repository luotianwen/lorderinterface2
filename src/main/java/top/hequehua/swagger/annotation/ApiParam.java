package top.hequehua.swagger.annotation;

import top.hequehua.swagger.config.DataType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 请求/返回参数
 *
 */
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiParam {
    /**
     * 参数名称
     *
     * @return
     */
    String name() default "";

    /**
     * 参数描述
     *
     * @return
     */
    String description() default "";

    /**
     * 是否必须
     *
     * @return
     */
    boolean required() default false;

    /**
     * 数据类型
     *
     * @return
     */
    String dataType() default DataType.String;

    /**
     * 默认值
     *
     * @return
     */
    String defaultValue() default "";

    /**
     * 参数类型
     */
    String paramType() default "query";
}
