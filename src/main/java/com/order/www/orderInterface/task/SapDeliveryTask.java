package com.order.www.orderInterface.task;

import com.jfinal.aop.Aop;
import com.jfinal.log.Log;
import com.order.www.orderInterface.service.OrderService;

public class SapDeliveryTask implements Runnable {
    Log log = Log.getLog(SapDeliveryTask.class);
    static OrderService orderService = Aop.get(OrderService.class);
    @Override
    public void run() {
        log.info("SapDeliveryTask begin");
        orderService.sapDelivery();
        log.info("SapDeliveryTask end");
    }
}
