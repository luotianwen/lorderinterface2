package com.order.www.orderInterface.task;

import com.jfinal.aop.Aop;
import com.jfinal.log.Log;
import com.order.www.orderInterface.service.OrderService;

/**
 * 订单处理任务
 * 订单集成B2B任务
 *@author martins
 */
public class OrderBatchTask implements Runnable {
    Log log = Log.getLog(OrderBatchTask.class);

    static OrderService orderService = Aop.get(OrderService.class);
    @Override
    public void run() {
        log.info("OrderBatchTask  begin");
        orderService.b2bbatch();
        log.info("OrderBatchTask end");

    }
}
