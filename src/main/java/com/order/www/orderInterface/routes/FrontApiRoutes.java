package com.order.www.orderInterface.routes;


import com.jfinal.config.Routes;
import com.order.www.orderInterface.controller.InterfaceController;
import com.order.www.orderInterface.controller.OrderController;

public class FrontApiRoutes extends Routes {

    @Override
    public void config() {

        add("/", InterfaceController.class);
        add("/order", OrderController.class);
       /* add("/print", OrderController.class,"/");*/
    }

}