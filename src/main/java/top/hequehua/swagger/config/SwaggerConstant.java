package top.hequehua.swagger.config;

import top.hequehua.swagger.annotation.ApiOperation;

import java.lang.annotation.Annotation;

/**
 */
public class SwaggerConstant {

    public static ApiOperation defaultApiOperation = new ApiOperation() {

        @Override
        public Class<? extends Annotation> annotationType() {
            return ApiOperation.class;
        }

        @Override
        public String value() {
            return "";
        }

        @Override
        public String[] tags() {
            return new String[]{};
        }

        @Override
        public String[] produces() {
            return new String[]{};
        }

        @Override
        public RequestMethod[] methods() {
            return new RequestMethod[]{};
        }

        @Override
        public String summary() {
            return null;
        }

        @Override
        public boolean hidden() {
            return true;
        }

        @Override
        public String description() {
            return "";
        }

        @Override
        public String[] consumes() {
            return new String[]{};
        }
    };

}
