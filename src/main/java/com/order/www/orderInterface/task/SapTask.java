package com.order.www.orderInterface.task;

import com.jfinal.aop.Aop;
import com.jfinal.log.Log;
import com.order.www.orderInterface.service.OrderService;

public class SapTask  implements Runnable {
    Log log = Log.getLog(SapTask.class);
    static OrderService orderService = Aop.get(OrderService.class);
    @Override
    public void run() {
        log.info("SapTask  begin");
         orderService.sapOrder();
        orderService.sapProfit();
        orderService.sapDelivery();
        log.info("SapTask  end");
    }
}

