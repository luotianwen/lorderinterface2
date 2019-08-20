package top.hequehua.swagger.model;

/**
 */
public class SwaggerApiInfo {

    private String name;
    private String url;
    private String location;
    private String swaggerVersion;
    private String description;
    private String title;

    public SwaggerApiInfo(String name, String url, String location) {
        this.name = name;
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        this.location = location;
        this.url = url + "?basePackage=" + this.location;
        this.swaggerVersion = "2.0";
    }

    public String getName() {
        return name;
    }

    public SwaggerApiInfo setName(String name) {
        this.name = name;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public SwaggerApiInfo setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getLocation() {
        return location;
    }

    public SwaggerApiInfo setLocation(String location) {
        this.location = location;
        return this;
    }

    public String getSwaggerVersion() {
        return swaggerVersion;
    }

    public SwaggerApiInfo setSwaggerVersion(String swaggerVersion) {
        this.swaggerVersion = swaggerVersion;
        return this;
    }

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
}
