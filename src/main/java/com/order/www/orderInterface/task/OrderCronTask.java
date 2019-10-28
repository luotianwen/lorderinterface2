package com.order.www.orderInterface.task;

import com.jfinal.aop.Aop;
import com.jfinal.log.Log;
import com.order.www.orderInterface.service.OrderService;

/**
 * 订单同步接口
 * @author martins
 */
public class OrderCronTask implements Runnable {
    Log log = Log.getLog(OrderCronTask.class);

    static OrderService orderService = Aop.get(OrderService.class);

    @Override
    public void run() {
        log.info("OrderCronTask  begin");
        orderService.orderCron();
        log.info("OrderCronTask  end");
    }


}
