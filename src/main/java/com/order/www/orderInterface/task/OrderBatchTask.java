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
        log.info("OrderB2BBatchTask  begin");
        orderService.b2bbatch();
        log.info("OrderB2BBatchTask end");
        log.info("OrderB2CBatchTask  begin");
        orderService.b2cMainbatch();
        orderService.b2cGiftbatch();
        log.info("OrderB2CBatchTask end");
    }
}
