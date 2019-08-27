package com.order.www.orderInterface.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jfinal.aop.Aop;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.order.www.orderInterface.common.OrderStatic;
import com.order.www.orderInterface.entity.*;
import com.order.www.orderInterface.service.OrderService;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 订单同步接口
 * @author martins
 */
public class OrderCronTask implements Runnable {
    Log log = Log.getLog(OrderCronTask.class);

    static OrderService orderService = Aop.get(OrderService.class);

    @Override
    public void run() {
        log.info("order task  begin");
        orderService.orderCron();

    }


}
