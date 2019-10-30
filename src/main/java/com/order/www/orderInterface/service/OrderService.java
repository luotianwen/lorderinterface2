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
     * 订单集合不合单，此类业务给出标识，按此标识识别订单类型
     */
    public void b2bbatch() {
        List<Record> ots = Db.find("select DISTINCT(pt.id)as id,pt.task_type as orderclass , pt.agentType ,pt.shipperID, pt.sapSupplierID  from pool_task pt,pool_task_line ptl " +
                "where pool_task_id=pt.id and  pt.task_type='1'   and  ptl.batch_num is null and date(pt.task_gen_datetime)<= DATE_SUB(CURDATE(),INTERVAL 1 DAY)");

        //List<Record> ots = Db.find("select id from pool_task where task_type='1' and  ptl.batch_num  is null and date(task_gen_datetime)<= DATE_SUB(CURDATE(),INTERVAL 1 DAY) ");
        //读取前一天的订单数据
        for (Record r : ots
        ) {
            List<TaskLine> tls = TaskLine.dao.getTls(r.getStr("id"));
            BigDecimal db = BigDecimal.ZERO;
            Date currentTime = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
            String dateString = "JC" + formatter.format(currentTime);
            Record rn = Db.findFirst("select BATCH_NUM as no from pool_batch where BATCH_NUM like '" + dateString + "%' order by BATCH_NUM desc");
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
            //System.out.println("b2bbatch-------"+no1);
            String no = no1;
            String bid = UUID.randomUUID().toString().replaceAll("-", "");
            Batch batch = new Batch();
            batch.setId(bid);
            batch.setBatchNum(no);
            batch.setOrderClass(r.getStr("orderclass"));
            batch.setAgentType(r.getStr("agentType"));
            batch.setSAPSupplierID(r.getStr("sapSupplierID"));
            //batch.setBatchCreator();
            batch.setBatchGenDatetime(new Date());
            batch.setShipperID(r.getStr("shipperID"));
            for (TaskLine tl : tls
            ) {


               // StockReData sr=getSapStockByItemCode(tl.getStr("product_no"));

                int sjkc = 0;//调取库存接口
                String ck = "A16";//B2B默认仓库
                 db = db.add(tl.getPayAmountSum());

                String oid = UUID.randomUUID().toString().replaceAll("-", "");
                BatchLine bl = new BatchLine();
                bl.setAgentType(r.getStr("agentType"));
                bl.setSAPSupplierID(r.getStr("sapSupplierID"));
                bl.setId(oid);
                bl.setPoolBatchId(bid);
                bl.setProductId(tl.getProductNo());
                bl.setSumPrice(tl.getPayAmount());
                bl.setAMOUNT(tl.getAmount());
                bl.setNAME(tl.getName());
                bl.setSupplierID(tl.getSupplierID());
                bl.setSupplierName(tl.getSupplierName());
                bl.setProductClass(tl.getProductClass());
                bl.setWhareHouse(ck);
                String finalCk = ck;
                Db.tx(() -> {
                    bl.save();
                    Db.update("update pool_task  set haveAmount='1' where id=?", tl.getPoolTaskId());
                    Db.update("update pool_task_line set batch_num =?,whareHouse=? where id=?", no, finalCk, tl.getId());
                    return true;
                });
            }
            batch.setCardCode(tls.get(0).getCardCode());
             batch.setSumAmt(db);
            Db.tx(() -> {
                batch.save();
                return true;
            });
        }
        try {
            Thread.sleep(3000L);
            b2cMainbatch();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    /**
     * 客户通过商城平台下单主商品并完成给平台的支付后，平台客服可指派某门店发货（场景A），如果门店拒绝接单或不具备发货条件，则客服可重新指派其它门店发货（场景B）。无论场景A还是场景B，都需要发货方确认货物已发出后，系统确认分润主体及金额。确认终端客户收到货后或货物发出一周后，平台公司通过系统向发货方分润99%，平台保留1%服务费。
     * 客户通过账号余额、微信等第三方支付平台完成支付，第三方支付通过T+1的时间周期，打款给平台，平台确认发货后完成分润，但不打款，待客户收到货后执行分润金额打款。
     */
    public void b2cMainbatch() {
         b2cWarhouse("1");

    }


    private StockReData getSapStockByItemCode(String itemCode){

        String json=OrderStatic.get(OrderStatic.salesstock+itemCode);
        StockReData sr=JSON.parseObject(json,StockReData.class);
        return sr;
    }
    public void b2cWarhouse(String product_Class) {

        List<Record> ots = Db.find("select ptl.product_no as no , pt.agentType , pt.sapSupplierID,pt.shipperID ,pt.sale_group as shipperType,pt.task_type as orderclass,sum(ptl.amount) amount from pool_task pt,pool_task_line ptl " +
                "where pool_task_id=pt.id and  pt.task_type='0' and ptl.product_Class='" + product_Class + "' and  ptl.batch_num is null and date(pt.task_gen_datetime)<= DATE_SUB(CURDATE(),INTERVAL 1 DAY) " +
                "GROUP BY    ptl.product_no,pt.agentType,pt.sapSupplierID,pt.shipperID,pt.sale_group ");

        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        String dateString = "JC"+ formatter.format(currentTime);
        Record rn = Db.findFirst("select BATCH_NUM as no from pool_batch where BATCH_NUM like '" + dateString + "%' order by BATCH_NUM desc");

        String no1 = "";
        if (rn != null) {
           // System.out.println(rn.toJson());
            no1 = rn.getStr("no");
        }
        int newNum = 0;
        String newStrNum = "";
        //读取前一天的订单数据
        for (Record r : ots
        ) {
            int sjkc = 100000;//调取库存接口
            String ck = "A16";
            //ShipperID=0  查询库存 做订单集成
            if(r.getInt("shipperID")==0) {
                StockReData sr = getSapStockByItemCode(r.getStr("no"));
                if (null != sr && sr.getCode().equals("0") && sr.getData().size() > 0) {
                    sjkc = sr.getData().get(0).getQuantity();
                    ck = sr.getData().get(0).getWharehouse();
                }
            }
            //ShipperID！=0 ShipperType=2  有物流单号  不查询库
            else {
                if (  "1".equals(r.getStr("shipperType"))) {
                    StockReData sr = getSapStockByItemCode(r.getStr("no"));
                    if (null != sr && sr.getCode().equals("0") && sr.getData().size() > 0) {
                        sjkc = sr.getData().get(0).getQuantity();
                        ck = sr.getData().get(0).getWharehouse();
                    }
                }
            }
            boolean save = false;


            BigDecimal db = BigDecimal.ZERO;
            BigDecimal db2 = BigDecimal.ZERO;


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
            batch.setOrderClass(r.getStr("orderclass"));
            batch.setAgentType(r.getStr("agentType"));
            batch.setSAPSupplierID(r.getStr("sapSupplierID"));
            batch.setShipperID(r.getStr("shipperID"));
            int amount = 0;
            batch.setBatchGenDatetime(new Date());
            List<TaskLine> tls = TaskLine.dao.getB2cTls(r.getStr("no"), product_Class,r.getStr("agentType"),r.getStr("sapSupplierID"));
            int amountCount=0;
            for (TaskLine tl : tls
            ) {
                sjkc = sjkc - tl.getAmount();
                if (sjkc < 0) {
                    Db.tx(() -> {
                        Db.update("update pool_task  set haveAmount=null where id=?", tl.getPoolTaskId());
                        return true;
                    });
                    break;
                }
                db = db.add(tl.getPayAmount());
                db2 = db2.add(tl.getPayAmountSum());
                amount = amount + tl.getAmount();
                amountCount++;

                String finalCk = ck;
                Db.tx(() -> {

                    Db.update("update pool_task  set haveAmount='1' where id=?", tl.getPoolTaskId());
                    Db.update("update pool_task_line set batch_num =?,whareHouse=? where id=?", no, finalCk, tl.getId());
                    return true;
                });

                save = true;
            }
            //一个都没有
            if (!save) {
                continue;
            }


            String oid = UUID.randomUUID().toString().replaceAll("-", "");

            TaskLine tl = tls.get(0);
            BatchLine bl = new BatchLine();
            bl.setId(oid);
            bl.setPoolBatchId(bid);
            bl.setProductId(tl.getProductNo());
            bl.setSumPrice(db.divide(new BigDecimal(amountCount),2,BigDecimal.ROUND_HALF_UP));
            bl.setAMOUNT(amount);
            bl.setNAME(tl.getName());
            bl.setSupplierID(tl.getSupplierID());
            bl.setSupplierName(tl.getSupplierName());
            bl.setProductClass(tl.getProductClass());
            bl.setWhareHouse(ck);
            bl.setAgentType(tl.getAgentType());
            bl.setSAPSupplierID(tl.getSAPSupplierID());
            batch.setCardCode(tl.getCardCode());
            batch.setSumAmt(db2);

            Db.tx(() -> {
                bl.save();
                batch.save();

                return true;
            });


        }

        try {
            Thread.sleep(1000L);

            if(product_Class.equals("1")) {
                b2cGiftbatch();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /**
     * 莲香岛科技通过买入卖出的方式销售礼品。客户通过商城平台下单礼品并完成给平台的支付后，莲香岛科技将礼品配送给客户，确认客户签收或发货一周后，平台公司将货款支付给莲香岛科技，扣除1%服务费。莲香岛科技通过外挂实现订单分拣、发货、称重、打印面单等操作。
     * 订单集合后，判断SAP物料库存是否满足，对满足的物料，按商品销售价格相同的订单，生成一笔订单记录，通过接口写入SAP，SAP生成交货单，并返回订单集合系统交货单号。交货单输入参数：总价1=商品销售单价1×商品数量1；总价2=商品销售单价2×商品数量2；。。。。。。缺货的订单不发货，系统记录后随时可查询，下一批次订单处理时优先处理之前的缺货订单，以缺货时间长短确定发货优先级。
     */
    public void b2cGiftbatch() {
        b2cWarhouse("2");



    }

    /**
     * 同步订单
     */
    public void orderCron() {
        Map map = new HashMap();
        map.put("Number", "10");

       /* String json=" {\n" +
                "    \"Status\": 200,\n" +
                "    \"Result\": {\n" +
                "        \"Total\": 1,\n" +
                "        \"Surplus\": 0,\n" +
                "        \"List\": [{\"OrderID\":\"LD201910211724580144648\",\"ReceiveName\":\"张帅伟\",\"Mobile\":\"13020831303\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019-10-21T17:25:01\",\"OrderAddress\":\"中铁国际城12号楼2单元1703室\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"朝阳区\",\"UserID\":77742,\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"丑\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"Items\":[{\"ItemCode\":\"888890-01\",\"QuanTity\":1,\"Price\":3300.00,\"ProductName\":\"新版净牌-雪莲滋养贴200\",\"Score\":800.00,\"SupplierID\":0,\"SupplierName\":\"平台供货\",\"ProductType\":1,\"PayAmount\":3300.00,\"PayAmountSum\":3300.00,\"PriceSum\":3300.00,\"ReductionAmount\":0.00,\"AgentType\":6511,\"SAPSupplierID\":\"900001\"}],\"ShipperType\":2,\"ShipperID\":0,\"ShipperName\":\"平台供货商\",\"PayableAmount\":3300.00,\"PayAmount\":3300.00,\"ReductionAmount\":0.00,\"Score\":800.00,\"AgentType\":6511,\"SAPSupplierID\":\"900001\"}\n" +

                "        ]\n" +
                "    },\n" +
                "    \"Msg\": \"成功\"\n" +
                "}";*/
        String json = OrderStatic.lxdpost(OrderStatic.SendOrder, map);
       // System.out.println(json);
        OrderJson oj = new OrderJson();
        oj.setContent(json);
        oj.setCreateDate(new Date());
        oj.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        oj.save();
       // System.out.println(json);
        log.info(json);
        ResponseEntity<OrderBean> datas = JSON.parseObject(json, new TypeReference<ResponseEntity<OrderBean>>(ResponseEntity.class, OrderBean.class, OrderEntity.class) {
        });
        if (datas.getStatus() != 200) {
            log.error("order task " + datas.getMsg());
        } else {
            List<OrderEntity> oes = datas.getResult().getList();


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
                    //tk.setRelievePrice(new BigDecimal(it.getPayAmountSum()));
                    tk.setSupplierID(it.getSupplierID());
                    tk.setSupplierName(it.getSupplierName());
                    tk.setProductClass(it.getProductType() + "");
                    tk.setPayAmount(new BigDecimal(it.getPayAmount()));
                    tk.setPayAmountSum(new BigDecimal(it.getPayAmountSum()));
                    tk.setSAPSupplierID(it.getSapSupplierID());
                    tk.setPriceSum(new BigDecimal(it.getPriceSum()));
                    tk.setReductionAmount(new BigDecimal(it.getReductionAmount()));
                    tk.setAgentType(it.getAgentType());
                    tk.setScore(new BigDecimal(it.getScore()));
                    tks.add(tk);
                   // db = db.add(new BigDecimal(it.getPrice()));
                }

                OrderTask ot = new OrderTask();
                ot.setTaskAmount(new BigDecimal(oe.getPayAmount()));
                ot.setCardCode(oe.getCardCode());
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
                ot.setCustomerNo(oe.getUserID());
                ot.setPoolTaskNo(no);
                ot.setTaskType(oe.getOrderClass() + "");
                ot.setDmNo(oe.getActivityID() + "");
                ot.setDmName(oe.getActivityName());
                ot.setTaskCreator(oe.getCreateUserName());
                ot.setRemark(oe.getRemark());
                ot.setCreateDate(new Date());
                ot.setSaleGroup(oe.getShipperType() + "");
                ot.setShipperName(oe.getShipperName());
                ot.setShipperID(oe.getShipperID() + "");
                ot.setPayableAmount(new BigDecimal(oe.getPayableAmount()));
                ot.setReductionAmount(new BigDecimal(oe.getReductionAmount()));
                ot.setScore(new BigDecimal(oe.getScore()));
                ot.setAgentType(oe.getAgentType());
                ot.setSAPSupplierID(oe.getSapSupplierID());
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


        for (TransferData transferData : orderReturns
        ) {
            List<TransferData.ItemBean> ibs = transferData.getItem();
            TaskLine tl = TaskLine.dao.findFirst("select id from pool_task_line where  product_class in('1','3','5') and task_no=? and product_no=?", transferData.getOrderID(), transferData.getItemCode());
            if (null == tl) {
                continue;
            }
            for (TransferData.ItemBean ib : ibs
            ) {
                String id = UUID.randomUUID().toString().replaceAll("-", "");
                TaskLineMoney tm = new TaskLineMoney();
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
        if (searchData.isSuccess()) {
//物流状态: 0-无轨迹，1-已揽收，2-在途中，3-签收,4-问题件
            if (3 == searchData.getState()) {
                Db.update("update pool_task  set recall_status=? where carriers like '%?%'", "1", searchData.getLogisticCode());
            } else if (4 == searchData.getState()) {
                Db.update("update pool_task  set recall_status=? where carriers like '%?%'", "2", searchData.getLogisticCode());
            }
            String id = UUID.randomUUID().toString().replace("-", "");
            Db.update("insert into pool_logistic values(?,?,?,?)", id, searchData.getLogisticCode(), JSON.toJSON(searchData), new Date());
        }
    }

    /**
     * 获取当天已发货的订单
     */
    public void orderDeliver() {
        List<OrderTask> ots = OrderTask.dao.getCurDeliverOrder();
        Map map = new HashMap();
        List<Record> ess = Db.find("select name , remarks from pool_express");

        for (OrderTask ot : ots) {
            map.clear();
            String[] cs = ot.getCarriers().split("\\s+");
            String LogisticsCode = "";

            for (Record es : ess) {
                if (es.getStr("name").contains(cs[0])) {
                    LogisticsCode = es.getStr("remarks");

                    break;
                }


            }
            if (StrKit.isBlank(LogisticsCode)) {
                continue;
            }
            map.put("OrderID", ot.getTaskNo());
            map.put("LogisticsNum", cs[1]);
            map.put("LogisticsCode", LogisticsCode);

            log.info("发送发货" +map.toString());
            String json = OrderStatic.lxdpost(OrderStatic.SendGoods, map);
            log.info("发送发货" +  ot.getTaskNo() + "   " + json);

            SendsGoodsData orderReturns=JSON.parseObject(json, SendsGoodsData.class);
             GetTransfer(orderReturns.getResult());

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
            //tk.setRelievePrice(new BigDecimal(it.getPayAmountSum()));
            tk.setSupplierID(it.getSupplierID());
            tk.setSupplierName(it.getSupplierName());
            tk.setProductClass(it.getProductType() + "");
            tk.setPayAmount(new BigDecimal(it.getPayAmount()));
            tk.setPayAmountSum(new BigDecimal(it.getPayAmountSum()));
            tk.setSAPSupplierID(it.getSapSupplierID());
            tk.setPriceSum(new BigDecimal(it.getPriceSum()));
            tk.setReductionAmount(new BigDecimal(it.getReductionAmount()));
            tk.setAgentType(it.getAgentType());
            tk.setScore(new BigDecimal(it.getScore()));
            tks.add(tk);

        }
        OrderTask ot = new OrderTask();
        ot.setTaskAmount(new BigDecimal(oe.getPayAmount()));
        ot.setCardCode(oe.getCardCode());
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
        ot.setCustomerNo(oe.getUserID());
        ot.setPoolTaskNo(no);
        ot.setTaskType(oe.getOrderClass() + "");
        ot.setDmNo(oe.getActivityID() + "");
        ot.setDmName(oe.getActivityName());
        ot.setTaskCreator(oe.getCreateUserName());
        ot.setRemark(oe.getRemark());
        ot.setCreateDate(new Date());
        ot.setSaleGroup(oe.getShipperType() + "");
        ot.setShipperName(oe.getShipperName());
        ot.setShipperID(oe.getShipperID() + "");
        ot.setPayableAmount(new BigDecimal(oe.getPayableAmount()));
        ot.setReductionAmount(new BigDecimal(oe.getReductionAmount()));
        ot.setScore(new BigDecimal(oe.getScore()));
        ot.setAgentType(oe.getAgentType());
        ot.setSAPSupplierID(oe.getSapSupplierID());



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
        Givemoney gv = new Givemoney();
        gv.setId(oid);
        gv.setAccountName(money.getAccountName());
        gv.setAccountNumber(money.getAccountNumber());
        gv.setAmount(new BigDecimal(money.getAmount()));
        gv.setBankName(money.getBankName());
        gv.setUserID(money.getUserID());
        gv.setUserType(money.getUserType());
        gv.setTypeName(money.getTypeName());
        gv.setTransferType(money.getTransferType());
        gv.setYeepayID(money.getYeepayID());
        List<GivemoneyOrder> gos = new ArrayList<>();

        for (GetMoney.OrderListBean ob : money.getOrderList()
        ) {
            GivemoneyOrder go = new GivemoneyOrder();
            go.setAmount(new BigDecimal(ob.getAmount()));
            go.setMoneyId(oid);
            go.setId(UUID.randomUUID().toString().replaceAll("-", ""));
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


    }

    @Before(Tx.class)
    public void changeOrderStatus(OrderEntity money) {
        Db.update("update pool_task set task_status=? where task_no=?", money.getStatus(), money.getOrderID());
    }
    /**
     * sap分润接口
     * 凭单接口
     */
    public void sapProfit() {
        List<Record> tasklist = Db.find("select DISTINCT pt.id ,pt.task_no from   pool_task pt, pool_task_line ptl, pool_task_line_money  ptlm where    ptlm.userType=3 and     pt.id=ptl.pool_task_id and ptl.id= ptlm.line_id and  ptlm.isok is  null");
        for (Record task : tasklist
        ) {
            List<Record> list = Db.find("select pt.task_no as platNo,pt.task_type as orderClass ,pt.pool_task_no as omsNo,pt.task_type as profitType,ptl.supplierID as shipperId,ptl.supplierName as shipperName,  ptl.product_class as shipperType , ptl.product_no as itemCode,ptlm.proportion as ratio,ptlm.amount,ptlm.id  from   pool_task pt, pool_task_line ptl, pool_task_line_money  ptlm where  ptlm.userType=3 and    pt.id=ptl.pool_task_id and ptl.id= ptlm.line_id and  ptlm.isok is  null and pt.id=?", task.getStr("id"));
            String param=JsonKit.toJson(list);
            String json = OrderStatic.post(OrderStatic.journal,param);
            log.info("凭单接口参数"+param);
            ResponseEntity responseEntity = JSON.parseObject(json, ResponseEntity.class);
            log.info("凭单接口结果"+JsonKit.toJson(responseEntity));
            if (responseEntity.getCode() == 0) {
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

            } else {
                log.error("同步sap凭单接口参数"+param);
                log.error("同步sap凭单失败" + task.getStr("task_no"));
            }

        }

    }
    /*
    销售订单接口
     */
    public void sapOrder() {
        List<Record> list = new ArrayList<>();
        List<Record> tasklist = Db.find("select *  from   pool_batch  where    ERP_NO is  null");
        for (Record task : tasklist
        ) {
            Record r = new Record();
            r.set("orderClass",task.getStr("orderClass"));
            r.set("cardCode",task.getStr("cardCode"));
            r.set("docDate",task.getDate("BATCH_GEN_DATETIME"));
            r.set("omsOrderNo",task.getStr("BATCH_NUM"));
            r.set("comments","");
            r.set("shipperID",task.getStr("shipperID"));
            r.set("agentType",task.getStr("agentType"));
            r.set("sapSupplierID",task.getStr("sapSupplierID"));
            List<Record> salesOrderLines=new ArrayList<>();
            List<Record> salesOrderLines1=Db.find("select * from pool_batch_line where POOL_BATCH_ID=?",task.getStr("id"));

            for (Record r11:salesOrderLines1
                 ) {
                Record r1=new Record();
                r1.set("omsOrderNo",task.getStr("BATCH_NUM"));
                r1.set("costingCode1","");
                r1.set("costingCode2","");
                r1.set("productType",r11.getStr("product_class"));
                r1.set("itemCode",r11.getStr("PRODUCT_ID"));
                r1.set("whsCode",r11.getStr("whareHouse"));
                r1.set("price",r11.getBigDecimal("SUM_PRICE"));
                r1.set("quantity",r11.getInt("AMOUNT"));
                r1.set("omsLineId",r11.getStr("id"));



                salesOrderLines.add(r1);
            }
            r.set("salesOrderLines",salesOrderLines);
            String param= JsonKit.toJson(r);
            String json = OrderStatic.post(OrderStatic.salesorder,param);
            //System.out.println(json);
            log.info("销售订单接口参数"+param);
            ResponseEntity responseEntity = JSON.parseObject(json, ResponseEntity.class);
            log.info("销售订单接口结果"+JsonKit.toJson(responseEntity));
           // System.out.println(JsonKit.toJson(responseEntity));
            if (responseEntity.getCode() == 0) {
                try {
                    Db.tx(() -> {
                        for (Record r1 : tasklist
                                ) {
                            String no =  UUID.randomUUID().toString().replaceAll("-", "");;
                            Db.update("update pool_batch set ERP_NO=? where id=?", no, r1.getStr("id"));
                            Db.update("update  pool_task_line   set  erp_no=? where  batch_num=?", no, r1.getStr("BATCH_NUM"));
                        }

                        return true;
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    log.error(e.getMessage());
                    log.error("销售订单接口参数"+param);
                    log.error("销售订单接口结果"+JsonKit.toJson(responseEntity));
                }
            }
            else{
                log.error("销售订单接口参数"+param);
                log.error("销售订单接口结果"+JsonKit.toJson(responseEntity));
            }
        }

    }
    /*
    销售交货接口
     */
    public void sapDelivery() {
        List<Record> tasklist = Db.find("select *  from   pool_batch  where    isok is  null");

        List<Record> list=new ArrayList<>();
        List<Record> list2=new ArrayList<>();
        for (Record task:tasklist) {
            List<Record> os = Db.find("select  *   from   pool_task pt,pool_task_line ptl where    pt.task_status='1' and  pt.id=ptl.pool_task_id and ptl.batch_num=?",task.getStr("BATCH_NUM"));
             if(null!=os&&os.size()>0)
            {
                continue;
            }
            list2.add(task);
            List<Record> salesOrderLines1=Db.find("select  ptl.* from pool_batch_line ptl,pool_batch pt  where pt.id=ptl.POOL_BATCH_ID    and ptl.POOL_BATCH_ID=?",task.getStr("id"));

            Record r=new Record();

            r.set("orderClass",task.getStr("orderClass"));
            r.set("cardCode",task.getStr("cardCode"));
            r.set("docDate",task.getDate("BATCH_GEN_DATETIME"));
            r.set("omsOrderNo",task.getStr("ERP_NO"));
            r.set("omsSourceNo",task.getStr("BATCH_NUM"));
            r.set("comments","");
            r.set("shipperID",task.getStr("shipperID"));
            r.set("agentType",task.getStr("agentType"));
            r.set("sapSupplierID",task.getStr("sapSupplierID"));

            List<Record> salesOrderLines=new ArrayList<>();

            for (Record r11:salesOrderLines1
            ) {
                Record ar=Db.findFirst("SELECT  IFNULL(sum(ptlm.amount),0)as amount   from pool_batch pb,pool_batch_line pbl,pool_task_line ptl,pool_task_line_money ptlm where ptl.id=ptlm.line_id and pb.BATCH_NUM=ptl.batch_num and pb.id=pbl.POOL_BATCH_ID " +
                        "and ptl.agentType=? and  ptl.sAPSupplierID=? and ptl.product_no=?  and ptlm.userType=2",r11.getStr("agentType") ,r11.getStr("sapSupplierID"),r11.getStr("PRODUCT_ID"));
                Record r1=new Record();
                r1.set("omsOrderNo",task.getStr("ERP_NO"));
                r1.set("omsSourceNo",task.getStr("BATCH_NUM"));
                r1.set("costingCode1","");
                r1.set("costingCode2","");
                r1.set("omsLineId",r11.getStr("id"));
                r1.set("omsSourceLineId",r11.getStr("id"));
                r1.set("productType",r11.getStr("product_class"));
                r1.set("itemCode",r11.getStr("PRODUCT_ID"));
                r1.set("whsCode",r11.getStr("whareHouse"));
                r1.set("price",ar.getDouble("amount"));//交货 2 供应商
                r1.set("quantity",r11.getInt("AMOUNT"));


                salesOrderLines.add(r1);
            }
            r.set("salesDeliveryLines",salesOrderLines);
            String param=JsonKit.toJson(r);
            String json = OrderStatic.post(OrderStatic.salesdelivery,param );
            log.info("销售交货接口参数"+param);
            ResponseEntity responseEntity = JSON.parseObject(json, ResponseEntity.class);
            log.info("销售交货接口结果"+JsonKit.toJson(responseEntity));

            if (responseEntity.getCode() == 0) {
                try {
                    Db.tx(() -> {
                        for (Record r1 : list2
                                ) {

                            Db.update("update pool_batch set isok=? where id=?", "1", r1.getStr("id"));
                        }

                        return true;
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    log.error(e.getMessage());
                    log.error("销售交货接口参数"+param);
                    log.error("销售交货接口结果"+JsonKit.toJson(responseEntity));
                }
            }
            else{
                log.error("销售交货接口参数"+param);
                log.error("销售交货接口结果"+JsonKit.toJson(responseEntity));
            }
        }


    }
}
