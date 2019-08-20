package top.hequehua.swagger.controller;

import com.jfinal.aop.Clear;
import com.jfinal.core.Controller;
import com.jfinal.kit.JsonKit;
import top.hequehua.swagger.config.SwaggerPlugin;
import top.hequehua.swagger.model.SwaggerDoc;

/**
 **/
@Clear
public class SwaggerController extends Controller {

    public static final String API_URL = "swagger/api";

    public void index() {
        render("doc.html");
    }

    public void api() {
        SwaggerDoc swaggerDoc = SwaggerPlugin.getDoc(getPara("basePackage"));
        renderJson(swaggerDoc == null ? "" : JsonKit.toJson(swaggerDoc));
    }

    public void swagger_resources() {
        renderJson(JsonKit.toJson(SwaggerPlugin.getApiInfo()));
    }

}