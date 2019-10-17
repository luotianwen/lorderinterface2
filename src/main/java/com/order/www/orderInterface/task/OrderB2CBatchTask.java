package com.order.www.orderInterface.task;

import com.jfinal.aop.Aop;
import com.jfinal.log.Log;
import com.order.www.orderInterface.service.OrderService;

/**
 * 订单处理任务
 * 订单集成B2C主品任务
 *@author martins
 */
public class OrderB2CBatchTask implements Runnable {
    Log log = Log.getLog(OrderB2CBatchTask.class);

    static OrderService orderService = Aop.get(OrderService.class);
    @Override
    public void run() {
        orderService.b2cMainbatch();
        orderService.b2cGiftbatch();
    }
}
