package top.hequehua.swagger.model;

import java.util.Map;

/**
 */
public class SwaggerDto {
    private String description;
    private Map<String, SwaggerDtoProperties> properties;
    private String title;
    private String type;


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, SwaggerDtoProperties> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, SwaggerDtoProperties> properties) {
        this.properties = properties;
    }
}
