package top.hequehua.swagger.routes;

import com.jfinal.config.Routes;
import top.hequehua.swagger.controller.SwaggerController;

/**
 **/
public class MySwaggerRoutes extends Routes {
    @Override
    public void config() {
        add("/swagger", SwaggerController.class, "/");
    }
}
