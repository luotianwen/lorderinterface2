package top.hequehua.swagger.model;

/**
 */
public class SwaggerSchema {
    private String $ref;

    public SwaggerSchema(String $ref) {
        this.$ref = $ref;
    }

    public String get$ref() {
        return $ref;
    }

    public void set$ref(String $ref) {
        this.$ref = $ref;
    }
}
