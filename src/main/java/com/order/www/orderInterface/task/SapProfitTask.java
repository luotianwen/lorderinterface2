package com.order.www.orderInterface.task;

import com.jfinal.aop.Aop;
import com.jfinal.log.Log;
import com.order.www.orderInterface.service.OrderService;

/**
 * sap分润接口
 * 凭单接口
 */
public class SapProfitTask implements Runnable {
    Log log = Log.getLog(SapProfitTask.class);
    static OrderService orderService = Aop.get(OrderService.class);
    @Override
    public void run() {
        orderService.sapProfit();
    }
}
