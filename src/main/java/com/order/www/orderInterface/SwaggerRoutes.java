package com.order.www.orderInterface;

import com.jfinal.config.Routes;
import live.autu.plugin.jfinal.swagger.controller.SwaggerController;

public class SwaggerRoutes extends Routes {

    @Override
    public void config() {
        setBaseViewPath("/WEB-INF/views");
        add("/swagger", SwaggerController.class);
    }

}