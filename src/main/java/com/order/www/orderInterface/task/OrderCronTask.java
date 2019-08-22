package com.order.www.orderInterface.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jfinal.log.Log;
import com.order.www.orderInterface.common.OrderStatic;
import com.order.www.orderInterface.entity.*;

import java.math.BigDecimal;
import java.util.*;

public class OrderCronTask implements Runnable {
    Log log = Log.getLog(OrderCronTask.class);

    @Override
    public void run() {
        log.info("order task  begin");
        Map map = new HashMap();
        map.put("Number", "100");
        String json="{\n" +
                "\t\"Status\": 200,\n" +
                "\t\"Msg\": \"ok\",\n" +
                "\t\"Result\": {\n" +
                "\t\t\"Total\": 1,\n" +
                "\t\t\"Surplus\": 100,\n" +
                "\t\t\"List\": [{\n" +
                "\t\t\t\"OrderID\": \"LD201904221639537911111\",\n" +
                "\t\t\t\"ReceiveName\": \"test\",\n" +
                "\t\t\t\"Mobile\": \"13888888888\",\n" +
                "\t\t\t\"CardCode\": \"\",\n" +
                "\t\t\t\"DocDueDate\": \"2019-5-6\",\n" +
                "\t\t\t\"OrderAddress\": \"北京市朝阳区某街道某号楼\",\n" +
                "\t\t\t\"ProName\": \"北京市\",\n" +
                "\t\t\t\"CityName\": \"北京市\",\n" +
                "\t\t\t\"DisName\": \"朝阳区\",\n" +
                "\t\t\t\"UserID\": \"12345\",\n" +
                "\t\t\t\"Remark\": \"test\",\n" +
                "\t\t\t\"Items\": [{\n" +
                "\t\t\t\t\"ItemCode\": \"88888888\",\n" +
                "\t\t\t\t\"QuanTity\": 1,\n" +
                "\t\t\t\t\"Price\": 10.00\n" +
                "\t\t\t}]\n" +
                "\t\t}]\n" +
                "\t}\n" +
                "}";
       // String json = OrderStatic.lxdpost(OrderStatic.SendOrder, map);
        ResponseEntity<OrderBean> datas = JSON.parseObject(json, new TypeReference<ResponseEntity<OrderBean>>(ResponseEntity.class, OrderBean.class, OrderEntity.class) {
        });
        if (datas.getStatus() != 200) {
            log.error("order task " + datas.getMsg());
        } else {
            List<OrderEntity> oes = datas.getResult().getList();
            BigDecimal db = BigDecimal.ZERO;
            for (OrderEntity oe : oes
                    ) {
                String oid = UUID.randomUUID().toString().replaceAll("-", "");
                List<OrderEntity.ItemsBean> obs = oe.getItems();
                for (OrderEntity.ItemsBean it : obs
                        ) {
                    String id = UUID.randomUUID().toString().replaceAll("-", "");
                    TaskLine tk=new TaskLine();
                    tk.setPoolTaskId(oid);
                    tk.setId(id);
                    tk.setTaskNo(oe.getOrderID());
                    tk.setProductNo(it.getItemCode());
                    tk.setAmount(it.getQuanTity());
                    tk.setRelievePrice(new BigDecimal(it.getPrice()));
                    tk.save();
                    db=db.add(new BigDecimal(it.getPrice()));
                }

                OrderTask ot = new OrderTask();
                ot.setTaskAmount(db);
                ot.setId(oid);
                ot.setTaskStatus(1);
                ot.setTaskNo(oe.getOrderID());
                ot.setTaskGenDatetime(oe.getDocDueDate());
                ot.setConsigneeName(oe.getReceiveName());
                ot.setConsigneePhone(oe.getMobile());
                ot.setAddressDetail(oe.getOrderAddress());
                ot.setAddressProvince(oe.getProName());
                ot.setAddressCity(oe.getCityName());
                ot.setAddressCounty(oe.getDisName());
                ot.setFax(oe.getUserID());
                ot.setRemark(oe.getRemark());
                ot.save();
            }
        }
    }


}
