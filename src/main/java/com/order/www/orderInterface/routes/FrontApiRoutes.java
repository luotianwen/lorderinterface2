package com.order.www.orderInterface.routes;


import com.jfinal.config.Routes;
import com.order.www.orderInterface.InterfaceController;

public class FrontApiRoutes extends Routes {

    @Override
    public void config() {

        add("/", InterfaceController.class);
    }

}