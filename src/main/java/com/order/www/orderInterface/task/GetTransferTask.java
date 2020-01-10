package com.order.www.orderInterface.task;

import com.jfinal.aop.Aop;
import com.jfinal.log.Log;
import com.order.www.orderInterface.service.OrderService;

public class GetTransferTask implements Runnable {
    Log log = Log.getLog(OrderDeliverTask.class);

    static OrderService orderService = Aop.get(OrderService.class);

    @Override
    public void run() {
       /* log.info("GetTransferTask 开始");
        String json = OrderStatic.lxdpost(OrderStatic.GetTransfer,new HashMap<>());
        System.out.println(json);
        ResponseEntity<OrderBean2> datas = JSON.parseObject(json, new TypeReference<ResponseEntity<OrderBean2>>(ResponseEntity.class, OrderBean2.class, TransferData.class) {
        });
        log.info(json);
        if (datas.getStatus() != 200) {
            System.out.println(datas.getMsg());
        } else {
            List<TransferData> oes = datas.getResult().getList();
            orderService.GetTransfer(oes);

        }
        log.info("GetTransferTask 结束");*/
        /* List<Record> as=Db.find("select `订单号` as d from a ");
        for (Record r:as 
             ) {
            for (int i = 0; i < 4; i++) {



            List<Record> ls= Db.find("SELECT l.id   from pool_task_line_money l where l.line_id=? and userType=? order by isok desc " ,r.getStr("d"),i);
                for (int j = 1; j <ls.size() ; j++) {
                    Db.update("delete from pool_task_line_money where id=?",ls.get(j).getStr("id"));
                }
            }
        }*/
       
    }
}
