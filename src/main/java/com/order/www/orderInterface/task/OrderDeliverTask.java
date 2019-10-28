package com.order.www.orderInterface.task;

import com.jfinal.aop.Aop;
import com.jfinal.log.Log;
import com.order.www.orderInterface.service.OrderService;

/**
 * 订单发货任务
 */
public class OrderDeliverTask implements Runnable{
    Log log = Log.getLog(OrderDeliverTask.class);

    static OrderService orderService = Aop.get(OrderService.class);

    @Override
    public void run() {
        log.info("OrderDeliverTask 开始");
        orderService.orderDeliver();
        log.info("OrderDeliverTask 结束");
    }
}
