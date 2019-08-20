package top.hequehua.swagger.model;

import top.hequehua.swagger.controller.SwaggerController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 */
public class SwaggerDoc {

    /**
     * 版本号
     */
    private String swagger = "2.0";

    private String host;

    private String basePackage;

    private String basePath = "/";

    private Map<String, SwaggerDto> definitions;

    private SwaggerApiInfo info;

    private Map<String, Map<String, SwaggerApiMethod>> paths = new HashMap<>();

    private List<SwaggerTag> tags = new ArrayList<>();

    public SwaggerDoc(String host, String basePackage, String name) {
        this.host = host;
        this.basePackage = basePackage;
        this.info = new SwaggerApiInfo(name, SwaggerController.API_URL, basePackage);
    }


    public void addTag(String name, String description) {
        tags.add(new SwaggerTag(name, description));
    }

    public List<SwaggerTag> getTags() {
        return tags;
    }

    public void setTags(List<SwaggerTag> tags) {
        this.tags = tags;
    }

    public String getSwagger() {
        return swagger;
    }

    public SwaggerDoc setSwagger(String swagger) {
        this.swagger = swagger;
        return this;
    }

    public SwaggerApiInfo getInfo() {
        return info;
    }

    public SwaggerDoc setInfo(SwaggerApiInfo info) {
        this.info = info.setLocation(basePackage);
        return this;
    }

    public String getHost() {
        return host;
    }

    public SwaggerDoc setHost(String host) {
        this.host = host;
        return this;
    }

    public Map<String, Map<String, SwaggerApiMethod>> getPaths() {
        return paths;
    }

    public SwaggerDoc setPaths(Map<String, Map<String, SwaggerApiMethod>> paths) {
        this.paths = paths;
        return this;
    }

    public String getbasePackage() {
        return basePackage;
    }

    public void setbasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    public Map<String, SwaggerDto> getDefinitions() {
        return definitions;
    }

    public void setDefinitions(Map<String, SwaggerDto> definitions) {
        this.definitions = definitions;
    }
}
