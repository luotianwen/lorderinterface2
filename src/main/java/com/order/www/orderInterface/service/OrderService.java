package com.order.www.orderInterface.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.order.www.orderInterface.common.OrderStatic;
import com.order.www.orderInterface.entity.*;
import com.order.www.orderInterface.task.OrderBatchTask;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 订单服务
 */
public class OrderService {
    Log log = Log.getLog(OrderService.class);

    /**
     * 生成订单接口服务
     * 订单集成 只做一个订单行数据的
     */
    public void batch() {
        List<Record> ots = Db.find("select id from pool_task where erp_no is null and date(task_gen_datetime)= DATE_SUB(CURDATE(),INTERVAL 1 DAY) ");
        //读取前一天的订单数据
        for (Record r : ots
                ) {
            List<TaskLine> tls = TaskLine.dao.getTls(r.getStr("id"));
            //订单行数据超过一个物料的不做订单集成
           /* if (tls.size() > 1 || tls.size() == 0) {
                continue;
            }*/
            BigDecimal db = BigDecimal.ZERO;
            Date currentTime = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
            String dateString = "JC" + formatter.format(currentTime);
            Record rn = Db.findFirst("select BATCH_NUM as no from pool_batch where BATCH_NUM like '" + dateString + "%' order by BATCH_GEN_DATETIME desc");
            String no1 = "";
            if (rn != null) {
                no1 = rn.getStr("no");
            }
            int newNum = 0;
            String newStrNum = "";
            if (StrKit.notBlank(no1)) {
                newNum = Integer.parseInt(no1.substring(10, no1.length()));
            }

            newNum++;
            //数字长度为5位，长度不够数字前面补0
            newStrNum = String.format("%05d", newNum);
            no1 = dateString + newStrNum;
            String no = no1;
            String bid = UUID.randomUUID().toString().replaceAll("-", "");
            Batch batch = new Batch();
            batch.setId(bid);
            batch.setBatchNum(no);

            //batch.setBatchCreator();
            batch.setBatchGenDatetime(new Date());
            for (TaskLine tl : tls
                    ) {
                db=db.add(tl.getRelievePrice());
                String oid = UUID.randomUUID().toString().replaceAll("-", "");
                BatchLine bl = new BatchLine();
                bl.setId(oid);
                bl.setPoolBatchId(bid);
                bl.setProductId(tl.getProductNo());
                bl.setSumPrice(tl.getRelievePrice());
                bl.setAMOUNT(tl.getAmount());
                bl.setNAME(tl.getName());
                Db.tx(() -> {
                    bl.save();
                    Db.update("update pool_task_line set batch_num =? where id=?", no, tl.getId());
                    return true;
                });
            }
            batch.setSumAmt(db);
            Db.tx(() -> {
                batch.save();
                return true;
            });
        }
    }

    /**
     * 同步订单
     */
    public void orderCron() {
        Map map = new HashMap();
        map.put("Number", "100");
        //String json="{\"Status\":200,\"Result\":{\"Total\":7,\"Surplus\":0,\"List\":[{\"OrderID\":\"LD201907091443107423814\",\"ReceiveName\":\"王祥\",\"Mobile\":\"15227904344\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/7/9 14:43:17\",\"OrderAddress\":\"天通苑北街道东沙各庄\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"昌平区\",\"UserID\":\"96097\",\"Remark\":\"\",\"Items\":[{\"ItemCode\":\"899335\",\"QuanTity\":1,\"Price\":0.0,\"CurrentScore\":0.0}]},{\"OrderID\":\"LD201907101046030277325\",\"ReceiveName\":\"王祥\",\"Mobile\":\"15227904344\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/7/10 10:46:04\",\"OrderAddress\":\"天通苑北街道东沙各庄\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"昌平区\",\"UserID\":\"96097\",\"Remark\":\"\",\"Items\":[{\"ItemCode\":\"899850\",\"QuanTity\":1,\"Price\":0.0,\"CurrentScore\":0.0}]},{\"OrderID\":\"LD201907101047021791306\",\"ReceiveName\":\"王祥\",\"Mobile\":\"15227904344\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/7/10 10:47:04\",\"OrderAddress\":\"天通苑北街道东沙各庄\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"昌平区\",\"UserID\":\"96097\",\"Remark\":\"\",\"Items\":[{\"ItemCode\":\"899335\",\"QuanTity\":1,\"Price\":0.0,\"CurrentScore\":0.0}]},{\"OrderID\":\"LD201907101050389833337\",\"ReceiveName\":\"王祥\",\"Mobile\":\"15227904344\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/7/10 10:50:39\",\"OrderAddress\":\"天通苑北街道东沙各庄\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"昌平区\",\"UserID\":\"96097\",\"Remark\":\"\",\"Items\":[{\"ItemCode\":\"899521\",\"QuanTity\":1,\"Price\":0.0,\"CurrentScore\":0.0}]},{\"OrderID\":\"LD201907241744437446397\",\"ReceiveName\":\"vbb\",\"Mobile\":\"16689876567\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/7/24 17:44:43\",\"OrderAddress\":\"宝宝不会后悔\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"西城区\",\"UserID\":\"74301\",\"Remark\":\"\",\"Items\":[{\"ItemCode\":\"899334\",\"QuanTity\":1,\"Price\":0.0,\"CurrentScore\":0.0}]},{\"OrderID\":\"LD201907251740289939288\",\"ReceiveName\":\"王祥\",\"Mobile\":\"15227904344\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/7/25 17:40:30\",\"OrderAddress\":\"天通苑北街道东沙各庄\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"昌平区\",\"UserID\":\"96097\",\"Remark\":\"\",\"Items\":[{\"ItemCode\":\"889041\",\"QuanTity\":1,\"Price\":0.0,\"CurrentScore\":0.0}]},{\"OrderID\":\"LD201907251741405318426\",\"ReceiveName\":\"王祥\",\"Mobile\":\"15227904344\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/7/25 17:41:42\",\"OrderAddress\":\"天通苑北街道东沙各庄\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"昌平区\",\"UserID\":\"96097\",\"Remark\":\"\",\"Items\":[{\"ItemCode\":\"889041\",\"QuanTity\":1,\"Price\":0.0,\"CurrentScore\":0.0}]}]},\"Msg\":\"成功\"}";
        String json = OrderStatic.lxdpost(OrderStatic.SendOrder, map);
        OrderJson oj=new OrderJson();
        oj.setContent(json);
        oj.setCreateDate(new Date());
        oj.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        oj.save();
        System.out.println(json);
        log.info(json);
        ResponseEntity<OrderBean> datas = JSON.parseObject(json, new TypeReference<ResponseEntity<OrderBean>>(ResponseEntity.class, OrderBean.class, OrderEntity.class) {
        });
        if (datas.getStatus() != 200) {
            log.error("order task " + datas.getMsg());
        } else {
            List<OrderEntity> oes = datas.getResult().getList();
            BigDecimal db = BigDecimal.ZERO;


            Date currentTime = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
            String dateString = "DD" + formatter.format(currentTime);
            Record r = Db.findFirst("select pool_task_no as no from pool_task where pool_task_no like '" + dateString + "%' order by create_date desc");
            String no1 = "";
            if (r != null) {
                no1 = r.getStr("no");
            }
            int newNum = 0;
            String newStrNum = "";
            if (StrKit.isBlank(no1)) {
                newStrNum = String.format("%05d", newNum);
            } else {
                newNum = Integer.parseInt(no1.substring(10, no1.length()));
            }


            for (OrderEntity oe : oes
                    ) {
                newNum++;
                //数字长度为5位，长度不够数字前面补0
                newStrNum = String.format("%05d", newNum);
                no1 = dateString + newStrNum;

                String no = no1;
                String oid = UUID.randomUUID().toString().replaceAll("-", "");
                List<OrderEntity.ItemsBean> obs = oe.getItems();
                List<TaskLine> tks = new ArrayList<>();
                for (OrderEntity.ItemsBean it : obs
                        ) {
                    String id = UUID.randomUUID().toString().replaceAll("-", "");
                    TaskLine tk = new TaskLine();
                    tk.setPoolTaskId(oid);
                    tk.setId(id);
                    tk.setTaskNo(oe.getOrderID());
                    tk.setProductNo(it.getItemCode());
                    tk.setAmount(it.getQuanTity());
                    tk.setName(it.getProductName());
                    tk.setLxbAmount(new BigDecimal(it.getScore()));
                    tk.setRelievePrice(new BigDecimal(it.getPrice()));
                    tks.add(tk);
                    db = db.add(new BigDecimal(it.getPrice()));
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
                ot.setPoolTaskNo(no);
                ot.setTaskType(oe.getOrderClass());
                ot.setDmNo(oe.getActivityID()+"");
                ot.setDmName(oe.getActivityName());
                ot.setTaskCreator(oe.getCreateUserName());
                ot.setRemark(oe.getRemark());
                ot.setCreateDate(new Date());
                for (TaskLine t : tks
                        ) {
                    t.setPoolTaskNo(no);
                }

                try {
                    Db.tx(() -> {
                        ot.save();
                        for (TaskLine t : tks
                                ) {
                            t.save();
                        }
                        return true;
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    log.error(e.getMessage());
                }

            }
        }
    }
    @Before(Tx.class)
    public void GetTransfer(List<TransferData> orderReturns) {


        for (TransferData transferData:orderReturns
             ) {
        List<TransferData.ItemBean> ibs=transferData.getItem();
            TaskLine tl=TaskLine.dao.findFirst("select id from pool_task_line where task_no=? and product_no=?",transferData.getOrderID(),transferData.getItemCode());
            if(null==tl){
                continue;
            }
            for (TransferData.ItemBean ib:ibs
                 ) {
                //用户类型 0 门店，1 代理商，2 供应商，3 平台，5 魅力合伙人
                if(3==ib.getUserType()){
                    tl.setProfitLsdinfoAmount(new BigDecimal(ib.getAmount()));
                    tl.setProfitLsdinfoRates(new BigDecimal(ib.getProportion()));
                }
                else if(0==ib.getUserType()){
                    tl.setProfitStoreAmount(new BigDecimal(ib.getAmount()));
                    tl.setProfitStoreRates(new BigDecimal(ib.getProportion()));
                }
                else if(1==ib.getUserType()){
                    tl.setProfitLsdtechAmount(new BigDecimal(ib.getAmount()));
                    tl.setProfitLsdtechRates(new BigDecimal(ib.getProportion()));
                }
                else if(2==ib.getUserType()){
                    tl.setProfitSupplierAmount(new BigDecimal(ib.getAmount()));
                    tl.setProfitSupplierRates(new BigDecimal(ib.getProportion()));
                }
            }
            tl.update();
        }
    }

    @Before(Tx.class)
    public void subscript(SearchData searchData) {
        if(searchData.isSuccess()){
//物流状态: 0-无轨迹，1-已揽收，2-在途中，3-签收,4-问题件
            if(3==searchData.getState()) {
                Db.update("update pool_task  set recall_status=? where carriers like '%?%'", "1", searchData.getLogisticCode());
            }
            else if(4==searchData.getState()){
                Db.update("update pool_task  set recall_status=? where carriers like '%?%'", "2", searchData.getLogisticCode());
            }
            String id=UUID.randomUUID().toString().replace("-", "");
            Db.update("insert into pool_logistic values(?,?,?,?)",id,searchData.getLogisticCode(),JSON.toJSON(searchData),new Date());
        }
    }
}
