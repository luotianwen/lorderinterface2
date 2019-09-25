package com.order.www.orderInterface.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jfinal.aop.Before;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.order.www.orderInterface.common.OrderStatic;
import com.order.www.orderInterface.entity.*;

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
     * 门店通过人客合一系统下单到商城平台并完成给平台的支付后，莲香岛科技将商品配送给门店，确认门店签收或发货一周后，平台公司将货款支付给莲香岛科技。莲香岛科技通过外挂实现订单分拣、发货、称重、打印面单等操作。
     订单集合不合单，此类业务给出标识，按此标识识别订单类型
     */
    public void b2bbatch() {
        List<Record> ots = Db.find("select id from pool_task where task_type='0' and  erp_no is null and date(task_gen_datetime)= DATE_SUB(CURDATE(),INTERVAL 1 DAY) ");
        //读取前一天的订单数据
        for (Record r : ots
                ) {
            List<TaskLine> tls = TaskLine.dao.getTls(r.getStr("id"));
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
     * 客户通过商城平台下单主商品并完成给平台的支付后，平台客服可指派某门店发货（场景A），如果门店拒绝接单或不具备发货条件，则客服可重新指派其它门店发货（场景B）。无论场景A还是场景B，都需要发货方确认货物已发出后，系统确认分润主体及金额。确认终端客户收到货后或货物发出一周后，平台公司通过系统向发货方分润99%，平台保留1%服务费。
     * 客户通过账号余额、微信等第三方支付平台完成支付，第三方支付通过T+1的时间周期，打款给平台，平台确认发货后完成分润，但不打款，待客户收到货后执行分润金额打款。
     */
    public void b2cMainbatch() {
        List<Record> ots = Db.find("select ptl.product_no as no ,sum(ptl.amount) amount from pool_task pt,pool_task_line ptl " +
                "where pool_task_id=pt.id and  pt.task_type='0' and ptl.product_Class='1' and  pt.erp_no is null and date(pt.task_gen_datetime)= DATE_SUB(CURDATE(),INTERVAL 1 DAY) " +
                "GROUP BY    ptl.product_no ");
        //读取前一天的订单数据
        for (Record r : ots
        ) {

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
             List<TaskLine> tls = TaskLine.dao.getB2cTls(r.getStr("id"),"1");
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
     * 莲香岛科技通过买入卖出的方式销售礼品。客户通过商城平台下单礼品并完成给平台的支付后，莲香岛科技将礼品配送给客户，确认客户签收或发货一周后，平台公司将货款支付给莲香岛科技，扣除1%服务费。莲香岛科技通过外挂实现订单分拣、发货、称重、打印面单等操作。
     * 订单集合后，判断SAP物料库存是否满足，对满足的物料，按商品销售价格相同的订单，生成一笔订单记录，通过接口写入SAP，SAP生成交货单，并返回订单集合系统交货单号。交货单输入参数：总价1=商品销售单价1×商品数量1；总价2=商品销售单价2×商品数量2；。。。。。。缺货的订单不发货，系统记录后随时可查询，下一批次订单处理时优先处理之前的缺货订单，以缺货时间长短确定发货优先级。
     */
    public void b2cGiftbatch() {

        List<Record> ots = Db.find("select ptl.product_no as no  ,sum(ptl.amount) amount  from pool_task pt,pool_task_line ptl " +
                "where pool_task_id=pt.id and  pt.task_type='0' and ptl.product_Class='2' and  pt.erp_no is null and date(pt.task_gen_datetime)= DATE_SUB(CURDATE(),INTERVAL 1 DAY) " +
                "GROUP BY    ptl.product_no ");
        //List<Record> ots = Db.find("select id from pool_task where task_type='0' and  erp_no is null and date(task_gen_datetime)= DATE_SUB(CURDATE(),INTERVAL 1 DAY) ");
        //读取前一天的订单数据
        for (Record r : ots
        ) {

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


            List<TaskLine> tls = TaskLine.dao.getB2cTls(r.getStr("id"),"2");
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
        map.put("Number", "10");
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
                    tk.setSupplierID(it.getSupplierID());
                    tk.setSupplierName(it.getSupplierName());
                    tk.setProductClass(it.getProductType()+"");
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
                ot.setTaskType(oe.getOrderClass()+"");
                ot.setDmNo(oe.getActivityID()+"");
                ot.setDmName(oe.getActivityName());
                ot.setTaskCreator(oe.getCreateUserName());
                ot.setRemark(oe.getRemark());
                ot.setCreateDate(new Date());
                ot.setSaleGroup(oe.getShipperType()+"");
                ot.setShipperName(oe.getShipperName());
                ot.setShipperID(oe.getShipperID()+"");
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
                String id = UUID.randomUUID().toString().replaceAll("-", "");
                TaskLineMoney tm=new TaskLineMoney();
                tm.setId(id);
                tm.setLineId(tl.getId());
                tm.setAmount(ib.getAmount());
                tm.setProportion(ib.getProportion());
                tm.setTypeName(ib.getTypeName());
                tm.setUserType(ib.getUserType());
                tm.save();

            }
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

    /**
     * 获取当天已发货的订单
     */
    public void orderDeliver() {
        List<OrderTask> ots=OrderTask.dao.getCurDeliverOrder();
        Map map = new HashMap();
        List<Record> ess=Db.find("select name , remarks from pool_express");

        for(OrderTask ot:ots){
            map.clear();
            String[] cs=ot.getCarriers().split("\\s+");
            String LogisticsNum="";
            for (Record es:ess){
                if(es.getStr("name").contains(cs[0])){
                    LogisticsNum=es.getStr("remarks");
                    break;
                }
            }
            if(StrKit.isBlank(LogisticsNum)) {
                continue;
            }
            map.put("OrderID",ot.getTaskNo());
            map.put("LogisticsNum",LogisticsNum);
            map.put("LogisticsCode",cs[1]);
            String json = OrderStatic.lxdpost(OrderStatic.SendGoods, map);
            log.info("发送发货"+ot.getTaskNo()+"   "+json);

        }
    }

    public void SendOrder(OrderEntity oe) {
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
            tk.setSupplierID(it.getSupplierID());
            tk.setSupplierName(it.getSupplierName());
            tk.setProductClass(it.getProductType()+"");
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
        ot.setTaskType(oe.getOrderClass()+"");
        ot.setDmNo(oe.getActivityID()+"");
        ot.setDmName(oe.getActivityName());
        ot.setTaskCreator(oe.getCreateUserName());
        ot.setRemark(oe.getRemark());
        ot.setCreateDate(new Date());
        ot.setShipperName(oe.getShipperName());
        ot.setShipperID(oe.getShipperID()+"");
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
    @Before(Tx.class)
    public void getMoney(GetMoney money) {
        String oid = UUID.randomUUID().toString().replaceAll("-", "");
        Givemoney gv=new Givemoney();
        gv.setId(oid);
        gv.setAccountName(money.getAccountName());
        gv.setAccountNumber(money.getAccountNumber());
        gv.setAmount(new BigDecimal(money.getAmount()));
        gv.setBankName(money.getBankName());
        gv.setUserID(money.getUserID());
        gv.setUserType(money.getUserType());
        gv.setTypeName(money.getTypeName());

        List<GivemoneyOrder> gos = new ArrayList<>();

        for (GetMoney.OrderListBean ob:money.getOrderList()
                ) {
            GivemoneyOrder go=new GivemoneyOrder();
            go.setAmount(new BigDecimal(ob.getAmount()));
            go.setMoneyId(oid);
            go.setId( UUID.randomUUID().toString().replaceAll("-", ""));
            go.setItemCode(ob.getItemCode());
            go.setOrderDate(ob.getOrderDate());
            go.setProductName(ob.getProductName());
            go.setProductNumber(ob.getProductNumber());
            gos.add(go);

        }
        try {
            Db.tx(() -> {
                gv.save();
                for (GivemoneyOrder t : gos
                        ) {
                    t.save();
                }
                return true;
            });

        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }

        System.out.println(money);
    }
    @Before(Tx.class)
    public void changeOrderStatus(OrderEntity money) {
        Db.update("update pool_task set task_status=? where task_no=?",money.getStatus(),money.getOrderID());
    }

    public void sapProfit() {
        List<Record> tasklist=Db.find(
                "select DISTINCT pt.id ,pt.task_no" +
                        "from   pool_task pt, pool_task_line ptl, pool_task_line_money  ptlm" +
                        "where     pt.id=ptl.pool_task_id and ptl.id= ptlm.line_id and  ptlm.isok is  null");
        for (Record task:tasklist
             ) {
            List<Record> list=Db.find(
                    "select pt.task_no as platNo,pt.pool_task_no as omsNo,pt.task_type as profitType,ptl.supplierID as shipperId,ptl.supplierName as shipperName," +
                    "ptl.product_class as shipperType , ptl.product_no as itemCode,ptlm.proportion as ratio,ptlm.amount,ptlm.id " +
                            "from   pool_task pt, pool_task_line ptl, pool_task_line_money  ptlm" +
                       "where     pt.id=ptl.pool_task_id and ptl.id= ptlm.line_id and  ptlm.isok is  null and pt.id=?",task.getStr("id"));
           String json= OrderStatic.post(OrderStatic.journal,JsonKit.toJson(list));
            ResponseEntity responseEntity=JSON.parseObject(json,ResponseEntity.class);
            if(responseEntity.getCode()==0) {
                try {
                    Db.tx(() -> {
                        for (Record r : list
                                ) {
                            Db.update("update pool_task_line_money set isok='0' where id=?", r.getStr("id"));
                        }

                        return true;
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    log.error(e.getMessage());
                }

            }else{
                log.error("同步sap凭单失败"+task.getStr("task_no"));
            }

        }

    }
}
