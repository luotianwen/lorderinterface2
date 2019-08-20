package top.hequehua.swagger.model;

/**
 */
public class SwaggerResponse {

    private String description;

    private SwaggerSchema schema;

    public SwaggerResponse(String description, SwaggerSchema schema) {
        this.description = description;
        this.schema = schema;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SwaggerSchema getSchema() {
        return schema;
    }

    public void setSchema(SwaggerSchema schema) {
        this.schema = schema;
    }

}
