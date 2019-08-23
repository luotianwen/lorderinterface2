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
        //String json="{\"Status\":200,\"Result\":{\"Total\":7,\"Surplus\":0,\"List\":[{\"OrderID\":\"LD201907091443107423814\",\"ReceiveName\":\"王祥\",\"Mobile\":\"15227904344\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/7/9 14:43:17\",\"OrderAddress\":\"天通苑北街道东沙各庄\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"昌平区\",\"UserID\":\"96097\",\"Remark\":\"\",\"Items\":[{\"ItemCode\":\"899335\",\"QuanTity\":1,\"Price\":0.0,\"CurrentScore\":0.0}]},{\"OrderID\":\"LD201907101046030277325\",\"ReceiveName\":\"王祥\",\"Mobile\":\"15227904344\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/7/10 10:46:04\",\"OrderAddress\":\"天通苑北街道东沙各庄\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"昌平区\",\"UserID\":\"96097\",\"Remark\":\"\",\"Items\":[{\"ItemCode\":\"899850\",\"QuanTity\":1,\"Price\":0.0,\"CurrentScore\":0.0}]},{\"OrderID\":\"LD201907101047021791306\",\"ReceiveName\":\"王祥\",\"Mobile\":\"15227904344\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/7/10 10:47:04\",\"OrderAddress\":\"天通苑北街道东沙各庄\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"昌平区\",\"UserID\":\"96097\",\"Remark\":\"\",\"Items\":[{\"ItemCode\":\"899335\",\"QuanTity\":1,\"Price\":0.0,\"CurrentScore\":0.0}]},{\"OrderID\":\"LD201907101050389833337\",\"ReceiveName\":\"王祥\",\"Mobile\":\"15227904344\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/7/10 10:50:39\",\"OrderAddress\":\"天通苑北街道东沙各庄\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"昌平区\",\"UserID\":\"96097\",\"Remark\":\"\",\"Items\":[{\"ItemCode\":\"899521\",\"QuanTity\":1,\"Price\":0.0,\"CurrentScore\":0.0}]},{\"OrderID\":\"LD201907241744437446397\",\"ReceiveName\":\"vbb\",\"Mobile\":\"16689876567\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/7/24 17:44:43\",\"OrderAddress\":\"宝宝不会后悔\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"西城区\",\"UserID\":\"74301\",\"Remark\":\"\",\"Items\":[{\"ItemCode\":\"899334\",\"QuanTity\":1,\"Price\":0.0,\"CurrentScore\":0.0}]},{\"OrderID\":\"LD201907251740289939288\",\"ReceiveName\":\"王祥\",\"Mobile\":\"15227904344\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/7/25 17:40:30\",\"OrderAddress\":\"天通苑北街道东沙各庄\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"昌平区\",\"UserID\":\"96097\",\"Remark\":\"\",\"Items\":[{\"ItemCode\":\"889041\",\"QuanTity\":1,\"Price\":0.0,\"CurrentScore\":0.0}]},{\"OrderID\":\"LD201907251741405318426\",\"ReceiveName\":\"王祥\",\"Mobile\":\"15227904344\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/7/25 17:41:42\",\"OrderAddress\":\"天通苑北街道东沙各庄\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"昌平区\",\"UserID\":\"96097\",\"Remark\":\"\",\"Items\":[{\"ItemCode\":\"889041\",\"QuanTity\":1,\"Price\":0.0,\"CurrentScore\":0.0}]}]},\"Msg\":\"成功\"}";
        String json = OrderStatic.lxdpost(OrderStatic.SendOrder, map);
        System.out.println(json);
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
