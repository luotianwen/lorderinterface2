package com.order.www.orderInterface.task;

import com.jfinal.aop.Aop;
import com.jfinal.log.Log;
import com.order.www.orderInterface.service.OrderService;

/**
 * 订单库存任务
 * 订单库存B2B任务
 *@author martins
 */
public class OrderKCBatchTask implements Runnable {
    Log log = Log.getLog(OrderKCBatchTask.class);

    static OrderService orderService = Aop.get(OrderService.class);
    @Override
    public void run() {
        log.info("OrderKCBatchTask  begin");
        orderService.b2bbatchkc();
        log.info("OrderKCBatchTask end");

    }
}
