package com.order.www.orderInterface.task;

import com.jfinal.aop.Aop;
import com.jfinal.log.Log;
import com.order.www.orderInterface.service.OrderService;

public class SapOrderTask implements Runnable {
    Log log = Log.getLog(SapOrderTask.class);
    static OrderService orderService = Aop.get(OrderService.class);
    @Override
    public void run() {
        log.info("SapOrderTask begin");
        orderService.sapOrder();
        log.info("SapOrderTask end");
    }
}
