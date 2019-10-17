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
        List<Record> ots = Db.find("select DISTINCT(pt.id)as id from pool_task pt,pool_task_line ptl " +
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
            batch.setOrderClass();
            //batch.setBatchCreator();
            batch.setBatchGenDatetime(new Date());
            Map map = new HashMap();
            List<TaskLine> tls2 = new ArrayList<>();
            for (TaskLine tl : tls
            ) {


                StockReData sr=getSapStockByItemCode(r.getStr("product_no"));

                int sjkc = 0;//调取库存接口
                String ck = "";
                if(null!=sr&&sr.getCode().equals("0")&&sr.getData().size()>0){
                    sjkc=sr.getData().get(0).getQuantity();
                    ck=sr.getData().get(0).getWharehouse();
                }

                if (tl.getAmount() <= sjkc) {
                    tl.setCardCode(ck);
                    tls2.add(tl);
                }
            }
            for (TaskLine tl : tls2
            ) {

                db = db.add(tl.getRelievePrice());
                String oid = UUID.randomUUID().toString().replaceAll("-", "");
                BatchLine bl = new BatchLine();
                bl.setId(oid);
                bl.setPoolBatchId(bid);
                bl.setProductId(tl.getProductNo());
                bl.setSumPrice(tl.getRelievePrice());
                bl.setAMOUNT(tl.getAmount());
                bl.setNAME(tl.getName());
                bl.setSupplierID(tl.getSupplierID());
                bl.setSupplierName(tl.getSupplierName());
                bl.setProductClass(tl.getProductClass());
                bl.setWhareHouse(tl.getCardCode());
                Db.tx(() -> {
                    bl.save();
                    Db.update("update pool_task  set haveAmount='1' where id=?", tl.getPoolTaskId());
                    Db.update("update pool_task_line set batch_num =?,whareHouse=? where id=?", no, tl.getCardCode(), tl.getId());
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
        List<Record> ots = Db.find("select ptl.product_no as no ,sum(ptl.amount) amount from pool_task pt,pool_task_line ptl " +
                "where pool_task_id=pt.id and  pt.task_type='0' and ptl.product_Class='" + product_Class + "' and  ptl.batch_num is null and date(pt.task_gen_datetime)<= DATE_SUB(CURDATE(),INTERVAL 1 DAY) " +
                "GROUP BY    ptl.product_no ");


        //读取前一天的订单数据
        for (Record r : ots
        ) {

            StockReData sr=getSapStockByItemCode(r.getStr("no"));

            int sjkc = 0;//调取库存接口
            String ck = "";
            if(null!=sr&&sr.getCode().equals("0")&&sr.getData().size()>0){
                sjkc=sr.getData().get(0).getQuantity();
                ck=sr.getData().get(0).getWharehouse();
            }

            boolean save = false;


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
            int amount = 0;
            batch.setBatchGenDatetime(new Date());
            List<TaskLine> tls = TaskLine.dao.getB2cTls(r.getStr("no"), product_Class);
            for (TaskLine tl : tls
            ) {
                sjkc = sjkc - tl.getAmount();
                if (sjkc < 0) {
                    break;
                }
                db = db.add(tl.getRelievePrice());
                amount = amount + tl.getAmount();


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
            bl.setSumPrice(db);
            bl.setAMOUNT(amount);
            bl.setNAME(tl.getName());
            bl.setSupplierID(tl.getSupplierID());
            bl.setSupplierName(tl.getSupplierName());
            bl.setProductClass(tl.getProductClass());
            bl.setWhareHouse(ck);

            batch.setCardCode(tl.getCardCode());
            batch.setSumAmt(db);
            Db.tx(() -> {
                bl.save();
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
        b2cWarhouse("2");

    }

    /**
     * 同步订单
     */
    public void orderCron() {
        Map map = new HashMap();
        map.put("Number", "10");

        //String json="{\"Status\":200,\"Result\":{\"Total\":100,\"Surplus\":168,\"List\":[{\"OrderID\":\"LD201909171728203998881\",\"ReceiveName\":\"迪家军\",\"Mobile\":\"18864879561\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/17 17:28:23\",\"OrderAddress\":\"是是是记得记得\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"西城区\",\"UserID\":\"96112\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"8P8FN04NV\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":1,\"ShipperID\":21,\"ShipperName\":\"门店供货商\",\"Items\":[{\"ItemCode\":\"888890-01\",\"QuanTity\":1,\"Price\":3300.00,\"ProductName\":\"新版净牌-雪莲滋养贴200片\",\"Score\":800.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0}]},{\"OrderID\":\"LD201909171738193668419\",\"ReceiveName\":\"就会v\",\"Mobile\":\"18577653356\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/17 17:38:19\",\"OrderAddress\":\"这些是你想什么\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"西城区\",\"UserID\":\"85445\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"J04N80Z80\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"899900\",\"QuanTity\":1,\"Price\":200.00,\"ProductName\":\"幸福时代经典炒锅-火象HXG-CG064\",\"Score\":0.00,\"SupplierID\":30,\"SupplierName\":\"北京千城万礼科贸有限公司\",\"ProductType\":0}]},{\"OrderID\":\"LD201909171740319872527\",\"ReceiveName\":\"迪家军\",\"Mobile\":\"18864879561\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/17 17:41:32\",\"OrderAddress\":\"是是是记得记得\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"西城区\",\"UserID\":\"96112\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"8P8FN04NV\",\"PayName\":\"混合支付\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":1,\"ShipperID\":21,\"ShipperName\":\"门店供货商\",\"Items\":[{\"ItemCode\":\"888890-01\",\"QuanTity\":1,\"Price\":3300.00,\"ProductName\":\"新版净牌-雪莲滋养贴200片\",\"Score\":800.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0}]},{\"OrderID\":\"LD201909171746163152322\",\"ReceiveName\":\"就会v\",\"Mobile\":\"18577653356\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/17 17:46:17\",\"OrderAddress\":\"这些是你想什么\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"西城区\",\"UserID\":\"85445\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"J04N80Z80\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":2,\"ShipperID\":0,\"ShipperName\":\"平台供货商\",\"Items\":[{\"ItemCode\":\"899334\",\"QuanTity\":1,\"Price\":597.01,\"ProductName\":\"净牌-雪莲滋养贴护垫-31片\",\"Score\":0.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0}]},{\"OrderID\":\"LD201909171800396887203\",\"ReceiveName\":\"刘凤双\",\"Mobile\":\"15100805298\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/17 18:00:49\",\"OrderAddress\":\"河北-沧州市-南皮县寨子镇十字街北200米路西\",\"ProName\":\"河北\",\"CityName\":\"沧州市\",\"DisName\":\"南皮县\",\"UserID\":\"9044\",\"Remark\":\"\",\"OrderClass\":1,\"UserName\":\"826F4R046\",\"PayName\":\"微信支付\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":2,\"ShipperID\":0,\"ShipperName\":\"平台供货商\",\"Items\":[{\"ItemCode\":\"900058\",\"QuanTity\":1,\"Price\":0.01,\"ProductName\":\"买雪莲滋养贴200片*18 送 雪莲滋养贴体验装*150 雪莲滋养贴100片*2 佛初草单瓶装*2 莲香岛定制丝巾*18\",\"Score\":0.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0}]},{\"OrderID\":\"LD201909171814578217045\",\"ReceiveName\":\"毛\",\"Mobile\":\"15034350606\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/17 18:15:23\",\"OrderAddress\":\"山西-临汾市-尧都区新春小区\",\"ProName\":\"山西\",\"CityName\":\"临汾市\",\"DisName\":\"尧都区\",\"UserID\":\"5251\",\"Remark\":\"\",\"OrderClass\":1,\"UserName\":\"maoxinfeng\",\"PayName\":\"微信支付\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"800766\",\"QuanTity\":5,\"Price\":1990.00,\"ProductName\":\"佛初草三瓶装\",\"Score\":0.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0},{\"ItemCode\":\"899335\",\"QuanTity\":1,\"Price\":0.00,\"ProductName\":\"净牌-雪莲滋养贴护垫-100片\",\"Score\":0.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0}]},{\"OrderID\":\"LD201909171820511354661\",\"ReceiveName\":\"刘凤双\",\"Mobile\":\"15100805298\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/17 18:21:08\",\"OrderAddress\":\"河北-沧州市-南皮县寨子镇十字街北200米路西\",\"ProName\":\"河北\",\"CityName\":\"沧州市\",\"DisName\":\"南皮县\",\"UserID\":\"9044\",\"Remark\":\"\",\"OrderClass\":1,\"UserName\":\"826F4R046\",\"PayName\":\"微信支付\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"900057\",\"QuanTity\":1,\"Price\":0.01,\"ProductName\":\"买佛初草三瓶装*10 送 佛初草单瓶装*2 佛初草体验装*8 莲香岛定制丝巾*10\",\"Score\":0.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0},{\"ItemCode\":\"900058\",\"QuanTity\":1,\"Price\":0.01,\"ProductName\":\"买雪莲滋养贴200片*18 送 雪莲滋养贴体验装*150 雪莲滋养贴100片*2 佛初草单瓶装*2 莲香岛定制丝巾*18\",\"Score\":0.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0}]},{\"OrderID\":\"LD201909171825457715644\",\"ReceiveName\":\"迪家军\",\"Mobile\":\"18864879561\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/17 18:25:49\",\"OrderAddress\":\"是是是记得记得\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"西城区\",\"UserID\":\"96112\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"8P8FN04NV\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"800779\",\"QuanTity\":1,\"Price\":0.00,\"ProductName\":\"净牌雪莲好伴侣卫生巾【日用】\",\"Score\":790.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0}]},{\"OrderID\":\"LD201909181026336274332\",\"ReceiveName\":\"张帅伟\",\"Mobile\":\"13020831303\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/18 10:26:35\",\"OrderAddress\":\"中铁国际城12号楼2单元1703室\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"朝阳区\",\"UserID\":\"77742\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"丑\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"888890-01\",\"QuanTity\":1,\"Price\":3380.00,\"ProductName\":\"新版净牌-雪莲滋养贴200片\",\"Score\":0.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0},{\"ItemCode\":\"899386-1\",\"QuanTity\":1,\"Price\":1380.01,\"ProductName\":\"佛初草单瓶装(赠品)\",\"Score\":0.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0}]},{\"OrderID\":\"LD201909181118421472188\",\"ReceiveName\":\"就会v\",\"Mobile\":\"18577653356\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/18 11:18:44\",\"OrderAddress\":\"这些是你想什么\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"西城区\",\"UserID\":\"85445\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"J04N80Z80\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":1,\"ShipperID\":3661,\"ShipperName\":\"门店供货商\",\"Items\":[{\"ItemCode\":\"899334\",\"QuanTity\":1,\"Price\":597.01,\"ProductName\":\"净牌-雪莲滋养贴护垫-31片\",\"Score\":0.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0}]},{\"OrderID\":\"LD201909181136242539097\",\"ReceiveName\":\"刘凤双\",\"Mobile\":\"15100805298\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/18 11:36:42\",\"OrderAddress\":\"河北-沧州市-南皮县寨子镇十字街北200米路西\",\"ProName\":\"河北\",\"CityName\":\"沧州市\",\"DisName\":\"南皮县\",\"UserID\":\"9044\",\"Remark\":\"\",\"OrderClass\":1,\"UserName\":\"826F4R046\",\"PayName\":\"微信支付\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":2,\"ShipperID\":0,\"ShipperName\":\"平台供货商\",\"Items\":[{\"ItemCode\":\"900057\",\"QuanTity\":1,\"Price\":0.01,\"ProductName\":\"买佛初草三瓶装*10 送 佛初草单瓶装*2 佛初草体验装*8 莲香岛定制丝巾*10\",\"Score\":0.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0},{\"ItemCode\":\"900058\",\"QuanTity\":1,\"Price\":0.01,\"ProductName\":\"买雪莲滋养贴200片*18 送 雪莲滋养贴体验装*150 雪莲滋养贴100片*2 佛初草单瓶装*2 莲香岛定制丝巾*18\",\"Score\":0.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0}]},{\"OrderID\":\"LD201909181505450342744\",\"ReceiveName\":\"白旭\",\"Mobile\":\"13811118624\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/18 15:05:47\",\"OrderAddress\":\"不知道\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"朝阳区\",\"UserID\":\"93838\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"白旭\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"899334\",\"QuanTity\":1,\"Price\":597.01,\"ProductName\":\"净牌-雪莲滋养贴护垫-31片\",\"Score\":0.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0}]},{\"OrderID\":\"LD201909181946071536915\",\"ReceiveName\":\"郑鹏2\",\"Mobile\":\"18513619603\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/18 19:46:30\",\"OrderAddress\":\"测试地址12345678\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"东城区\",\"UserID\":\"96140\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"X0RZ2PZ4B\",\"PayName\":\"微信支付\",\"ActivityType\":2,\"ActivityTypeName\":\"限时抢购\",\"ActivityID\":950,\"ActivityName\":\"测试重复支付问题\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"899894\",\"QuanTity\":1,\"Price\":0.01,\"ProductName\":\"隆力奇蛇油护手霜-50g\",\"Score\":0.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0}]},{\"OrderID\":\"LD201909181956333637392\",\"ReceiveName\":\"郑鹏2\",\"Mobile\":\"18513619603\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/18 19:56:47\",\"OrderAddress\":\"测试地址12345678\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"东城区\",\"UserID\":\"96140\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"X0RZ2PZ4B\",\"PayName\":\"微信支付\",\"ActivityType\":2,\"ActivityTypeName\":\"限时抢购\",\"ActivityID\":950,\"ActivityName\":\"测试重复支付问题\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"899894\",\"QuanTity\":1,\"Price\":0.01,\"ProductName\":\"隆力奇蛇油护手霜-50g\",\"Score\":0.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0}]},{\"OrderID\":\"LD201909182023019014882\",\"ReceiveName\":\"郑鹏2\",\"Mobile\":\"18513619603\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/18 20:23:19\",\"OrderAddress\":\"测试地址12345678\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"东城区\",\"UserID\":\"96140\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"X0RZ2PZ4B\",\"PayName\":\"微信支付\",\"ActivityType\":2,\"ActivityTypeName\":\"限时抢购\",\"ActivityID\":950,\"ActivityName\":\"测试重复支付问题\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"899894\",\"QuanTity\":1,\"Price\":0.01,\"ProductName\":\"隆力奇蛇油护手霜-50g\",\"Score\":0.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0}]},{\"OrderID\":\"LD201909182034074892687\",\"ReceiveName\":\"郑鹏2\",\"Mobile\":\"18513619603\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/18 20:34:19\",\"OrderAddress\":\"测试地址12345678\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"东城区\",\"UserID\":\"96140\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"X0RZ2PZ4B\",\"PayName\":\"微信支付\",\"ActivityType\":2,\"ActivityTypeName\":\"限时抢购\",\"ActivityID\":950,\"ActivityName\":\"测试重复支付问题\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"899894\",\"QuanTity\":1,\"Price\":0.01,\"ProductName\":\"隆力奇蛇油护手霜-50g\",\"Score\":0.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0}]},{\"OrderID\":\"LD201909182038241616005\",\"ReceiveName\":\"郑鹏2\",\"Mobile\":\"18513619603\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/18 20:38:34\",\"OrderAddress\":\"测试地址12345678\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"东城区\",\"UserID\":\"96140\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"X0RZ2PZ4B\",\"PayName\":\"微信支付\",\"ActivityType\":2,\"ActivityTypeName\":\"限时抢购\",\"ActivityID\":950,\"ActivityName\":\"测试重复支付问题\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"899894\",\"QuanTity\":1,\"Price\":0.01,\"ProductName\":\"隆力奇蛇油护手霜-50g\",\"Score\":0.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0}]},{\"OrderID\":\"LD201909190846163334431\",\"ReceiveName\":\"123\",\"Mobile\":\"18811484968\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/19 8:46:27\",\"OrderAddress\":\"123456\",\"ProName\":\"四川\",\"CityName\":\"绵阳市\",\"DisName\":\"三台县\",\"UserID\":\"81404\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"灰灰超酷\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"900100\",\"QuanTity\":1,\"Price\":0.00,\"ProductName\":\"花王酵素去污洗衣液（900g）-蓝色\",\"Score\":490.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0}]},{\"OrderID\":\"LD201909190924447094126\",\"ReceiveName\":\"123\",\"Mobile\":\"18811484968\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/19 9:24:50\",\"OrderAddress\":\"123456\",\"ProName\":\"四川\",\"CityName\":\"绵阳市\",\"DisName\":\"三台县\",\"UserID\":\"81404\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"灰灰超酷\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"900100\",\"QuanTity\":1,\"Price\":0.00,\"ProductName\":\"花王酵素去污洗衣液（900g）-蓝色\",\"Score\":490.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0}]},{\"OrderID\":\"LD201909191003146548215\",\"ReceiveName\":\"白旭\",\"Mobile\":\"13811118624\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/19 10:04:02\",\"OrderAddress\":\"不知道\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"朝阳区\",\"UserID\":\"93838\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"白旭\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"899137\",\"QuanTity\":2,\"Price\":1280.00,\"ProductName\":\"净牌雪莲宫净贴 30片\",\"Score\":0.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0},{\"ItemCode\":\"899334\",\"QuanTity\":5,\"Price\":597.01,\"ProductName\":\"净牌-雪莲滋养贴护垫-31片\",\"Score\":0.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0}]},{\"OrderID\":\"LD201909191011217182020\",\"ReceiveName\":\"白旭\",\"Mobile\":\"13811118624\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/19 10:11:39\",\"OrderAddress\":\"不知道\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"朝阳区\",\"UserID\":\"93838\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"白旭\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"888907\",\"QuanTity\":1,\"Price\":2980.00,\"ProductName\":\"绅元圣草养护贴\",\"Score\":0.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0},{\"ItemCode\":\"899334\",\"QuanTity\":4,\"Price\":597.01,\"ProductName\":\"净牌-雪莲滋养贴护垫-31片\",\"Score\":0.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0},{\"ItemCode\":\"888890-01\",\"QuanTity\":1,\"Price\":3300.00,\"ProductName\":\"新版净牌-雪莲滋养贴200片\",\"Score\":800.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0},{\"ItemCode\":\"800431-2\",\"QuanTity\":1,\"Price\":99.50,\"ProductName\":\"净牌-雪莲滋养贴护垫-1片试用装*5\",\"Score\":0.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0},{\"ItemCode\":\"899507\",\"QuanTity\":1,\"Price\":1980.00,\"ProductName\":\"雪莲-乳腺贴护垫-180片\",\"Score\":0.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0}]},{\"OrderID\":\"LD201909191021183417194\",\"ReceiveName\":\"123\",\"Mobile\":\"18811484968\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/19 10:21:30\",\"OrderAddress\":\"123456\",\"ProName\":\"四川\",\"CityName\":\"绵阳市\",\"DisName\":\"三台县\",\"UserID\":\"81404\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"灰灰超酷\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"800766\",\"QuanTity\":1,\"Price\":3980.00,\"ProductName\":\"佛初草三瓶装\",\"Score\":0.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0}]},{\"OrderID\":\"LD201909191036421496841\",\"ReceiveName\":\"123\",\"Mobile\":\"18811484968\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/19 10:36:53\",\"OrderAddress\":\"123456\",\"ProName\":\"四川\",\"CityName\":\"绵阳市\",\"DisName\":\"三台县\",\"UserID\":\"81404\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"灰灰超酷\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"899334\",\"QuanTity\":1,\"Price\":597.01,\"ProductName\":\"净牌-雪莲滋养贴护垫-31片\",\"Score\":0.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0}]},{\"OrderID\":\"LD201909191124255242445\",\"ReceiveName\":\"123\",\"Mobile\":\"18811484968\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/19 11:27:12\",\"OrderAddress\":\"123456\",\"ProName\":\"四川\",\"CityName\":\"绵阳市\",\"DisName\":\"三台县\",\"UserID\":\"81404\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"灰灰超酷\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"899334\",\"QuanTity\":1,\"Price\":597.01,\"ProductName\":\"净牌-雪莲滋养贴护垫-31片\",\"Score\":0.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0}]},{\"OrderID\":\"LD201909191131299527815\",\"ReceiveName\":\"白旭\",\"Mobile\":\"13811118624\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/19 11:31:32\",\"OrderAddress\":\"不知道\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"朝阳区\",\"UserID\":\"93838\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"白旭\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"899334\",\"QuanTity\":2,\"Price\":597.01,\"ProductName\":\"净牌-雪莲滋养贴护垫-31片\",\"Score\":0.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0},{\"ItemCode\":\"899386-1\",\"QuanTity\":1,\"Price\":1380.01,\"ProductName\":\"佛初草单瓶装(赠品)\",\"Score\":0.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0}]},{\"OrderID\":\"LD201909191133587666345\",\"ReceiveName\":\"白旭\",\"Mobile\":\"13811118624\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/19 11:34:00\",\"OrderAddress\":\"不知道\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"朝阳区\",\"UserID\":\"93838\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"白旭\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"899334\",\"QuanTity\":2,\"Price\":597.01,\"ProductName\":\"净牌-雪莲滋养贴护垫-31片\",\"Score\":0.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0},{\"ItemCode\":\"899386-1\",\"QuanTity\":1,\"Price\":1380.01,\"ProductName\":\"佛初草单瓶装(赠品)\",\"Score\":0.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0}]},{\"OrderID\":\"LD201909191324526396804\",\"ReceiveName\":\"就会v\",\"Mobile\":\"18577653356\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/19 13:24:55\",\"OrderAddress\":\"这些是你想什么\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"西城区\",\"UserID\":\"85445\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"J04N80Z80\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"899954\",\"QuanTity\":1,\"Price\":336.00,\"ProductName\":\"至简至纯含银海藻纤维补水面膜（10片/盒）\",\"Score\":0.00,\"SupplierID\":31,\"SupplierName\":\"厦门竡美科技有限公司\",\"ProductType\":0},{\"ItemCode\":\"899894\",\"QuanTity\":1,\"Price\":15.00,\"ProductName\":\"隆力奇蛇油护手霜-50g\",\"Score\":0.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0}]},{\"OrderID\":\"LD201909191328050933713\",\"ReceiveName\":\"就会v\",\"Mobile\":\"18577653356\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/19 13:28:11\",\"OrderAddress\":\"这些是你想什么\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"西城区\",\"UserID\":\"85445\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"J04N80Z80\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"899954\",\"QuanTity\":1,\"Price\":336.00,\"ProductName\":\"至简至纯含银海藻纤维补水面膜（10片/盒）\",\"Score\":0.00,\"SupplierID\":31,\"SupplierName\":\"厦门竡美科技有限公司\",\"ProductType\":0},{\"ItemCode\":\"899894\",\"QuanTity\":1,\"Price\":0.00,\"ProductName\":\"隆力奇蛇油护手霜-50g\",\"Score\":150.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0},{\"ItemCode\":\"899894\",\"QuanTity\":1,\"Price\":15.00,\"ProductName\":\"隆力奇蛇油护手霜-50g\",\"Score\":0.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0}]},{\"OrderID\":\"LD201909191336181965607\",\"ReceiveName\":\"白旭\",\"Mobile\":\"13811118624\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/19 13:36:21\",\"OrderAddress\":\"不知道\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"朝阳区\",\"UserID\":\"93838\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"白旭\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"899334\",\"QuanTity\":2,\"Price\":597.01,\"ProductName\":\"净牌-雪莲滋养贴护垫-31片\",\"Score\":0.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0},{\"ItemCode\":\"899386-1\",\"QuanTity\":1,\"Price\":1380.01,\"ProductName\":\"佛初草单瓶装(赠品)\",\"Score\":0.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0}]},{\"OrderID\":\"LD201909191339339209758\",\"ReceiveName\":\"白旭\",\"Mobile\":\"13811118624\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/19 13:39:41\",\"OrderAddress\":\"不知道\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"朝阳区\",\"UserID\":\"93838\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"白旭\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"899334\",\"QuanTity\":2,\"Price\":597.01,\"ProductName\":\"净牌-雪莲滋养贴护垫-31片\",\"Score\":0.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0},{\"ItemCode\":\"899386-1\",\"QuanTity\":1,\"Price\":1380.01,\"ProductName\":\"佛初草单瓶装(赠品)\",\"Score\":0.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0}]},{\"OrderID\":\"LD201909191346139075870\",\"ReceiveName\":\"王祥\",\"Mobile\":\"15227904344\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/19 13:48:19\",\"OrderAddress\":\"天通苑北街道东沙各庄\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"昌平区\",\"UserID\":\"96097\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"王祥\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"899679-1\",\"QuanTity\":10,\"Price\":19.80,\"ProductName\":\"足癣康\",\"Score\":0.00,\"SupplierID\":1,\"SupplierName\":\"猪猪供货商\",\"ProductType\":0}]},{\"OrderID\":\"LD201909191350459382563\",\"ReceiveName\":\"王祥\",\"Mobile\":\"15227904344\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/19 13:50:50\",\"OrderAddress\":\"天通苑北街道东沙各庄\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"昌平区\",\"UserID\":\"96097\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"王祥\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"899569\",\"QuanTity\":4,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0}]},{\"OrderID\":\"LD201909191353238639627\",\"ReceiveName\":\"白旭\",\"Mobile\":\"13811118624\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/19 13:53:27\",\"OrderAddress\":\"不知道\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"朝阳区\",\"UserID\":\"93838\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"白旭\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"809204\",\"QuanTity\":1,\"Price\":59.01,\"ProductName\":\"浓姜红糖\",\"Score\":39.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0},{\"ItemCode\":\"899502\",\"QuanTity\":1,\"Price\":208.00,\"ProductName\":\"平月白茶\",\"Score\":520.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0},{\"ItemCode\":\"899568\",\"QuanTity\":1,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0},{\"ItemCode\":\"899334\",\"QuanTity\":1,\"Price\":597.01,\"ProductName\":\"净牌-雪莲滋养贴护垫-31片\",\"Score\":0.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0},{\"ItemCode\":\"805579\",\"QuanTity\":1,\"Price\":398.00,\"ProductName\":\"天门东安螯合钙\",\"Score\":0.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0},{\"ItemCode\":\"899531\",\"QuanTity\":1,\"Price\":1280.00,\"ProductName\":\"爱丽丝\",\"Score\":320.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0}]},{\"OrderID\":\"LD201909191405430183057\",\"ReceiveName\":\"白旭\",\"Mobile\":\"13811118624\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/19 14:05:45\",\"OrderAddress\":\"不知道\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"朝阳区\",\"UserID\":\"93838\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"白旭\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"809204\",\"QuanTity\":1,\"Price\":59.01,\"ProductName\":\"浓姜红糖\",\"Score\":39.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0},{\"ItemCode\":\"899502\",\"QuanTity\":1,\"Price\":208.00,\"ProductName\":\"平月白茶\",\"Score\":520.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0},{\"ItemCode\":\"899568\",\"QuanTity\":1,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0},{\"ItemCode\":\"899334\",\"QuanTity\":1,\"Price\":597.01,\"ProductName\":\"净牌-雪莲滋养贴护垫-31片\",\"Score\":0.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0},{\"ItemCode\":\"805579\",\"QuanTity\":1,\"Price\":398.00,\"ProductName\":\"天门东安螯合钙\",\"Score\":0.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0},{\"ItemCode\":\"899531\",\"QuanTity\":1,\"Price\":1280.00,\"ProductName\":\"爱丽丝\",\"Score\":320.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0}]},{\"OrderID\":\"LD201909191405164388141\",\"ReceiveName\":\"王祥\",\"Mobile\":\"15227904344\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/19 14:07:15\",\"OrderAddress\":\"天通苑北街道东沙各庄\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"昌平区\",\"UserID\":\"96097\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"王祥\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"899569\",\"QuanTity\":4,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0}]},{\"OrderID\":\"LD201909191410531889759\",\"ReceiveName\":\"白旭\",\"Mobile\":\"13811118624\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/19 14:10:55\",\"OrderAddress\":\"不知道\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"朝阳区\",\"UserID\":\"93838\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"白旭\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"809204\",\"QuanTity\":1,\"Price\":59.01,\"ProductName\":\"浓姜红糖\",\"Score\":39.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0},{\"ItemCode\":\"899502\",\"QuanTity\":1,\"Price\":208.00,\"ProductName\":\"平月白茶\",\"Score\":520.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0},{\"ItemCode\":\"899568\",\"QuanTity\":1,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0},{\"ItemCode\":\"899334\",\"QuanTity\":1,\"Price\":597.01,\"ProductName\":\"净牌-雪莲滋养贴护垫-31片\",\"Score\":0.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0},{\"ItemCode\":\"805579\",\"QuanTity\":1,\"Price\":398.00,\"ProductName\":\"天门东安螯合钙\",\"Score\":0.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0},{\"ItemCode\":\"899531\",\"QuanTity\":1,\"Price\":1280.00,\"ProductName\":\"爱丽丝\",\"Score\":320.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0}]},{\"OrderID\":\"LD201909191416250587318\",\"ReceiveName\":\"白旭\",\"Mobile\":\"13811118624\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/19 14:16:31\",\"OrderAddress\":\"不知道\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"朝阳区\",\"UserID\":\"93838\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"白旭\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"809204\",\"QuanTity\":5,\"Price\":59.01,\"ProductName\":\"浓姜红糖\",\"Score\":39.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0},{\"ItemCode\":\"899568\",\"QuanTity\":5,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0}]},{\"OrderID\":\"LD201909191425530295105\",\"ReceiveName\":\"就会v\",\"Mobile\":\"18577653356\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/19 14:25:55\",\"OrderAddress\":\"这些是你想什么\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"西城区\",\"UserID\":\"85445\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"J04N80Z80\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"809204\",\"QuanTity\":5,\"Price\":59.01,\"ProductName\":\"浓姜红糖\",\"Score\":39.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0},{\"ItemCode\":\"899568\",\"QuanTity\":1,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0},{\"ItemCode\":\"899894\",\"QuanTity\":9,\"Price\":350.00,\"ProductName\":\"隆力奇蛇油护手霜-50g\",\"Score\":0.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0}]},{\"OrderID\":\"LD201909191430335307261\",\"ReceiveName\":\"王祥\",\"Mobile\":\"15227904344\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/19 14:30:35\",\"OrderAddress\":\"天通苑北街道东沙各庄\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"昌平区\",\"UserID\":\"96097\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"王祥\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"809204\",\"QuanTity\":5,\"Price\":59.01,\"ProductName\":\"浓姜红糖\",\"Score\":39.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0}]},{\"OrderID\":\"LD201909191409535604130\",\"ReceiveName\":\"王祥\",\"Mobile\":\"15227904344\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/19 14:42:56\",\"OrderAddress\":\"天通苑北街道东沙各庄\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"昌平区\",\"UserID\":\"96097\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"王祥\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"899569\",\"QuanTity\":4,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0}]},{\"OrderID\":\"LD201909191509104545618\",\"ReceiveName\":\"就会v\",\"Mobile\":\"18577653356\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/19 15:09:11\",\"OrderAddress\":\"这些是你想什么\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"西城区\",\"UserID\":\"85445\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"J04N80Z80\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"899894\",\"QuanTity\":1,\"Price\":0.00,\"ProductName\":\"隆力奇蛇油护手霜-50g\",\"Score\":150.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0}]},{\"OrderID\":\"LD201909191516512206937\",\"ReceiveName\":\"白旭\",\"Mobile\":\"13811118624\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/19 15:16:51\",\"OrderAddress\":\"不知道\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"朝阳区\",\"UserID\":\"93838\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"白旭\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"899334\",\"QuanTity\":1,\"Price\":597.01,\"ProductName\":\"净牌-雪莲滋养贴护垫-31片\",\"Score\":0.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0}]},{\"OrderID\":\"LD201909191516512294324\",\"ReceiveName\":\"白旭\",\"Mobile\":\"13811118624\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/19 15:16:51\",\"OrderAddress\":\"不知道\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"朝阳区\",\"UserID\":\"93838\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"白旭\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"809204\",\"QuanTity\":1,\"Price\":59.01,\"ProductName\":\"浓姜红糖\",\"Score\":39.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0},{\"ItemCode\":\"899502\",\"QuanTity\":1,\"Price\":208.00,\"ProductName\":\"平月白茶\",\"Score\":520.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0},{\"ItemCode\":\"805579\",\"QuanTity\":1,\"Price\":398.00,\"ProductName\":\"天门东安螯合钙\",\"Score\":0.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0},{\"ItemCode\":\"899531\",\"QuanTity\":1,\"Price\":1280.00,\"ProductName\":\"爱丽丝\",\"Score\":320.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0}]},{\"OrderID\":\"LD201909191516512395907\",\"ReceiveName\":\"白旭\",\"Mobile\":\"13811118624\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/19 15:16:51\",\"OrderAddress\":\"不知道\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"朝阳区\",\"UserID\":\"93838\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"白旭\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"899568\",\"QuanTity\":1,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0}]},{\"OrderID\":\"LD201909191536100316917\",\"ReceiveName\":\"就会v\",\"Mobile\":\"18577653356\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/19 15:36:49\",\"OrderAddress\":\"这些是你想什么\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"西城区\",\"UserID\":\"85445\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"J04N80Z80\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"809204\",\"QuanTity\":1,\"Price\":59.01,\"ProductName\":\"浓姜红糖\",\"Score\":39.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0},{\"ItemCode\":\"899568\",\"QuanTity\":1,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0},{\"ItemCode\":\"899569\",\"QuanTity\":1,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0},{\"ItemCode\":\"899570\",\"QuanTity\":1,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0},{\"ItemCode\":\"899571\",\"QuanTity\":1,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0}]},{\"OrderID\":\"LD201909191549008567337\",\"ReceiveName\":\"就会v\",\"Mobile\":\"18577653356\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/19 15:49:03\",\"OrderAddress\":\"这些是你想什么\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"西城区\",\"UserID\":\"85445\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"J04N80Z80\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"809204\",\"QuanTity\":1,\"Price\":59.01,\"ProductName\":\"浓姜红糖\",\"Score\":39.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0},{\"ItemCode\":\"899568\",\"QuanTity\":4,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0}]},{\"OrderID\":\"LD201909191552438886758\",\"ReceiveName\":\"就会v\",\"Mobile\":\"18577653356\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/19 15:52:43\",\"OrderAddress\":\"这些是你想什么\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"西城区\",\"UserID\":\"85445\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"J04N80Z80\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"809204\",\"QuanTity\":1,\"Price\":59.01,\"ProductName\":\"浓姜红糖\",\"Score\":39.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0}]},{\"OrderID\":\"LD201909191552439074357\",\"ReceiveName\":\"就会v\",\"Mobile\":\"18577653356\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/19 15:52:43\",\"OrderAddress\":\"这些是你想什么\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"西城区\",\"UserID\":\"85445\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"J04N80Z80\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"899568\",\"QuanTity\":4,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0}]},{\"OrderID\":\"LD201909191540553327138\",\"ReceiveName\":\"王祥\",\"Mobile\":\"15227904344\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/19 15:54:17\",\"OrderAddress\":\"天通苑北街道东沙各庄\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"昌平区\",\"UserID\":\"96097\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"王祥\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"809204\",\"QuanTity\":1,\"Price\":59.01,\"ProductName\":\"浓姜红糖\",\"Score\":39.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0},{\"ItemCode\":\"899568\",\"QuanTity\":4,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0}]},{\"OrderID\":\"LD201909191554496345223\",\"ReceiveName\":\"王祥\",\"Mobile\":\"15227904344\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/19 15:54:49\",\"OrderAddress\":\"天通苑北街道东沙各庄\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"昌平区\",\"UserID\":\"96097\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"王祥\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"809204\",\"QuanTity\":1,\"Price\":59.01,\"ProductName\":\"浓姜红糖\",\"Score\":39.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0}]},{\"OrderID\":\"LD201909191554496441859\",\"ReceiveName\":\"王祥\",\"Mobile\":\"15227904344\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/19 15:54:49\",\"OrderAddress\":\"天通苑北街道东沙各庄\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"昌平区\",\"UserID\":\"96097\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"王祥\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"899568\",\"QuanTity\":4,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0}]},{\"OrderID\":\"LD201909191556373612448\",\"ReceiveName\":\"就会v\",\"Mobile\":\"18577653356\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/19 15:56:39\",\"OrderAddress\":\"这些是你想什么\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"西城区\",\"UserID\":\"85445\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"J04N80Z80\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"809204\",\"QuanTity\":1,\"Price\":59.01,\"ProductName\":\"浓姜红糖\",\"Score\":39.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0},{\"ItemCode\":\"899568\",\"QuanTity\":4,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0}]},{\"OrderID\":\"LD201909191556524602128\",\"ReceiveName\":\"就会v\",\"Mobile\":\"18577653356\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/19 15:56:52\",\"OrderAddress\":\"这些是你想什么\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"西城区\",\"UserID\":\"85445\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"J04N80Z80\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"809204\",\"QuanTity\":1,\"Price\":59.01,\"ProductName\":\"浓姜红糖\",\"Score\":39.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0}]},{\"OrderID\":\"LD201909191556524706220\",\"ReceiveName\":\"就会v\",\"Mobile\":\"18577653356\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/19 15:56:52\",\"OrderAddress\":\"这些是你想什么\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"西城区\",\"UserID\":\"85445\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"J04N80Z80\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"899568\",\"QuanTity\":4,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0}]},{\"OrderID\":\"LD201909191601239617425\",\"ReceiveName\":\"呼噜娃\",\"Mobile\":\"15910325153\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/19 16:01:31\",\"OrderAddress\":\"后沙峪三山新新家园30栋6单元501室\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"顺义区\",\"UserID\":\"75828\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"899568\",\"QuanTity\":1,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0},{\"ItemCode\":\"899569\",\"QuanTity\":1,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0},{\"ItemCode\":\"899570\",\"QuanTity\":1,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0},{\"ItemCode\":\"899571\",\"QuanTity\":1,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0}]},{\"OrderID\":\"LD201909191603088588152\",\"ReceiveName\":\"白旭\",\"Mobile\":\"13811118624\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/19 16:03:14\",\"OrderAddress\":\"不知道\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"朝阳区\",\"UserID\":\"93838\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"白旭\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"899568\",\"QuanTity\":1,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0},{\"ItemCode\":\"899569\",\"QuanTity\":1,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0},{\"ItemCode\":\"899570\",\"QuanTity\":1,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0},{\"ItemCode\":\"899571\",\"QuanTity\":1,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0}]},{\"OrderID\":\"LD201909191713102017762\",\"ReceiveName\":\"白旭\",\"Mobile\":\"13811118624\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/19 17:13:15\",\"OrderAddress\":\"不知道\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"朝阳区\",\"UserID\":\"93838\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"白旭\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"809204\",\"QuanTity\":10,\"Price\":59.01,\"ProductName\":\"浓姜红糖\",\"Score\":39.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0},{\"ItemCode\":\"899568\",\"QuanTity\":10,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0},{\"ItemCode\":\"899569\",\"QuanTity\":10,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0}]},{\"OrderID\":\"LD201909191719371997232\",\"ReceiveName\":\"白旭\",\"Mobile\":\"13811118624\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/19 17:19:39\",\"OrderAddress\":\"不知道\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"朝阳区\",\"UserID\":\"93838\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"白旭\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"809204\",\"QuanTity\":2,\"Price\":59.01,\"ProductName\":\"浓姜红糖\",\"Score\":39.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0},{\"ItemCode\":\"899568\",\"QuanTity\":1,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0},{\"ItemCode\":\"899569\",\"QuanTity\":1,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0},{\"ItemCode\":\"899570\",\"QuanTity\":1,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0},{\"ItemCode\":\"899334\",\"QuanTity\":1,\"Price\":597.01,\"ProductName\":\"净牌-雪莲滋养贴护垫-31片\",\"Score\":0.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0},{\"ItemCode\":\"889144\",\"QuanTity\":1,\"Price\":3800.00,\"ProductName\":\"10mm淡水珠狮子头项链\",\"Score\":200.00,\"SupplierID\":6,\"SupplierName\":\"北京诗珊国际品牌顾问有限公司\",\"ProductType\":0},{\"ItemCode\":\"889145\",\"QuanTity\":1,\"Price\":980.00,\"ProductName\":\"16mm吊坠铁塔\",\"Score\":0.00,\"SupplierID\":6,\"SupplierName\":\"北京诗珊国际品牌顾问有限公司\",\"ProductType\":0}]},{\"OrderID\":\"LD201909191730050785130\",\"ReceiveName\":\"就会v\",\"Mobile\":\"18577653356\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/19 17:30:10\",\"OrderAddress\":\"这些是你想什么\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"西城区\",\"UserID\":\"85445\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"J04N80Z80\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"809204\",\"QuanTity\":1,\"Price\":59.01,\"ProductName\":\"浓姜红糖\",\"Score\":39.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0},{\"ItemCode\":\"899569\",\"QuanTity\":1,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0},{\"ItemCode\":\"899568\",\"QuanTity\":1,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0},{\"ItemCode\":\"899570\",\"QuanTity\":1,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0},{\"ItemCode\":\"899571\",\"QuanTity\":1,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0}]},{\"OrderID\":\"LD201909191731099179649\",\"ReceiveName\":\"王祥\",\"Mobile\":\"15227904344\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/19 17:31:42\",\"OrderAddress\":\"天通苑北街道东沙各庄\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"昌平区\",\"UserID\":\"96097\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"王祥\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"899568\",\"QuanTity\":1,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0},{\"ItemCode\":\"899569\",\"QuanTity\":1,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0},{\"ItemCode\":\"899570\",\"QuanTity\":1,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0},{\"ItemCode\":\"899571\",\"QuanTity\":1,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0}]},{\"OrderID\":\"LD201909191736046773469\",\"ReceiveName\":\"王祥\",\"Mobile\":\"15227904344\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/19 17:36:28\",\"OrderAddress\":\"天通苑北街道东沙各庄\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"昌平区\",\"UserID\":\"96097\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"王祥\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"809204\",\"QuanTity\":2,\"Price\":59.01,\"ProductName\":\"浓姜红糖\",\"Score\":39.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0},{\"ItemCode\":\"899568\",\"QuanTity\":1,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0},{\"ItemCode\":\"899569\",\"QuanTity\":1,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0},{\"ItemCode\":\"899570\",\"QuanTity\":1,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0}]},{\"OrderID\":\"LD201909191737470547047\",\"ReceiveName\":\"就会v\",\"Mobile\":\"18577653356\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/19 17:37:51\",\"OrderAddress\":\"这些是你想什么\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"西城区\",\"UserID\":\"85445\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"J04N80Z80\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"809204\",\"QuanTity\":1,\"Price\":59.01,\"ProductName\":\"浓姜红糖\",\"Score\":39.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0},{\"ItemCode\":\"899568\",\"QuanTity\":2,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0},{\"ItemCode\":\"899569\",\"QuanTity\":2,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0},{\"ItemCode\":\"899570\",\"QuanTity\":1,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0},{\"ItemCode\":\"899571\",\"QuanTity\":1,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0}]},{\"OrderID\":\"LD201909191516379805202\",\"ReceiveName\":\"王祥\",\"Mobile\":\"15227904344\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/19 17:39:42\",\"OrderAddress\":\"天通苑北街道东沙各庄\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"昌平区\",\"UserID\":\"96097\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"王祥\",\"PayName\":\"混合支付\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"899569\",\"QuanTity\":4,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0}]},{\"OrderID\":\"LD201909191749135712757\",\"ReceiveName\":\"就会v\",\"Mobile\":\"18577653356\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/19 17:50:35\",\"OrderAddress\":\"这些是你想什么\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"西城区\",\"UserID\":\"85445\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"J04N80Z80\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"809204\",\"QuanTity\":1,\"Price\":350.00,\"ProductName\":\"浓姜红糖\",\"Score\":0.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0}]},{\"OrderID\":\"LD201909191806191998952\",\"ReceiveName\":\"白旭\",\"Mobile\":\"13811118624\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/19 18:06:23\",\"OrderAddress\":\"不知道\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"朝阳区\",\"UserID\":\"93838\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"白旭\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":1230,\"CouponName\":\"全场通用优惠券100元\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"809204\",\"QuanTity\":1,\"Price\":59.01,\"ProductName\":\"浓姜红糖\",\"Score\":39.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0},{\"ItemCode\":\"899568\",\"QuanTity\":1,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0},{\"ItemCode\":\"899569\",\"QuanTity\":1,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0},{\"ItemCode\":\"899570\",\"QuanTity\":1,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0},{\"ItemCode\":\"899571\",\"QuanTity\":2,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0},{\"ItemCode\":\"899334\",\"QuanTity\":1,\"Price\":597.01,\"ProductName\":\"净牌-雪莲滋养贴护垫-31片\",\"Score\":0.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0}]},{\"OrderID\":\"LD201909191828555459827\",\"ReceiveName\":\"王祥\",\"Mobile\":\"15227904344\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/19 18:29:25\",\"OrderAddress\":\"天通苑北街道东沙各庄\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"昌平区\",\"UserID\":\"96097\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"王祥\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":1233,\"CouponName\":\"全场通用优惠券100元\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"809204\",\"QuanTity\":2,\"Price\":59.01,\"ProductName\":\"浓姜红糖\",\"Score\":39.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0},{\"ItemCode\":\"899568\",\"QuanTity\":4,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0}]},{\"OrderID\":\"LD201909191832274193973\",\"ReceiveName\":\"白旭\",\"Mobile\":\"13811118624\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/19 18:32:32\",\"OrderAddress\":\"不知道\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"朝阳区\",\"UserID\":\"93838\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"白旭\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":1229,\"CouponName\":\"全场通用优惠券100元\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"809204\",\"QuanTity\":1,\"Price\":59.01,\"ProductName\":\"浓姜红糖\",\"Score\":39.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0},{\"ItemCode\":\"899568\",\"QuanTity\":1,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0},{\"ItemCode\":\"899569\",\"QuanTity\":1,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0},{\"ItemCode\":\"899570\",\"QuanTity\":1,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0},{\"ItemCode\":\"899571\",\"QuanTity\":1,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0},{\"ItemCode\":\"899334\",\"QuanTity\":1,\"Price\":597.01,\"ProductName\":\"净牌-雪莲滋养贴护垫-31片\",\"Score\":0.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0}]},{\"OrderID\":\"LD201909191833031577722\",\"ReceiveName\":\"王祥\",\"Mobile\":\"15227904344\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/19 18:33:03\",\"OrderAddress\":\"天通苑北街道东沙各庄\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"昌平区\",\"UserID\":\"96097\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"王祥\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":1233,\"CouponName\":\"全场通用优惠券100元\",\"ShipperType\":2,\"ShipperID\":0,\"ShipperName\":\"平台供货商\",\"Items\":[{\"ItemCode\":\"809204\",\"QuanTity\":2,\"Price\":59.01,\"ProductName\":\"浓姜红糖\",\"Score\":39.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0}]},{\"OrderID\":\"LD201909191833031678955\",\"ReceiveName\":\"王祥\",\"Mobile\":\"15227904344\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/19 18:33:03\",\"OrderAddress\":\"天通苑北街道东沙各庄\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"昌平区\",\"UserID\":\"96097\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"王祥\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":1233,\"CouponName\":\"全场通用优惠券100元\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"899568\",\"QuanTity\":4,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0}]},{\"OrderID\":\"LD201909191856412525860\",\"ReceiveName\":\"就会v\",\"Mobile\":\"18577653356\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/19 18:56:44\",\"OrderAddress\":\"这些是你想什么\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"西城区\",\"UserID\":\"85445\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"J04N80Z80\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"809204\",\"QuanTity\":2,\"Price\":59.01,\"ProductName\":\"浓姜红糖\",\"Score\":39.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0},{\"ItemCode\":\"899568\",\"QuanTity\":1,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0},{\"ItemCode\":\"899569\",\"QuanTity\":1,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0},{\"ItemCode\":\"899570\",\"QuanTity\":2,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0}]},{\"OrderID\":\"LD201909191932498532622\",\"ReceiveName\":\"123\",\"Mobile\":\"18811484968\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/19 19:32:52\",\"OrderAddress\":\"123456\",\"ProName\":\"四川\",\"CityName\":\"绵阳市\",\"DisName\":\"三台县\",\"UserID\":\"81404\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"灰灰超酷\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"900100\",\"QuanTity\":1,\"Price\":0.00,\"ProductName\":\"花王酵素去污洗衣液（900g）-蓝色\",\"Score\":490.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0},{\"ItemCode\":\"807064-01\",\"QuanTity\":1,\"Price\":944.00,\"ProductName\":\"钻袖白小礼服\",\"Score\":236.00,\"SupplierID\":27,\"SupplierName\":\"北京依品科技有限公司\",\"ProductType\":0},{\"ItemCode\":\"899334\",\"QuanTity\":1,\"Price\":597.01,\"ProductName\":\"净牌-雪莲滋养贴护垫-31片\",\"Score\":0.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0}]},{\"OrderID\":\"LD201909191938390216587\",\"ReceiveName\":\"就会v\",\"Mobile\":\"18577653356\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/19 19:38:42\",\"OrderAddress\":\"这些是你想什么\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"西城区\",\"UserID\":\"85445\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"J04N80Z80\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"809204\",\"QuanTity\":1,\"Price\":59.01,\"ProductName\":\"浓姜红糖\",\"Score\":39.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0},{\"ItemCode\":\"899568\",\"QuanTity\":1,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0},{\"ItemCode\":\"899569\",\"QuanTity\":1,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0},{\"ItemCode\":\"899570\",\"QuanTity\":1,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0},{\"ItemCode\":\"899571\",\"QuanTity\":1,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0}]},{\"OrderID\":\"LD201909191950550461855\",\"ReceiveName\":\"就会v\",\"Mobile\":\"18577653356\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/19 19:50:58\",\"OrderAddress\":\"这些是你想什么\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"西城区\",\"UserID\":\"85445\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"J04N80Z80\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"809204\",\"QuanTity\":1,\"Price\":59.01,\"ProductName\":\"浓姜红糖\",\"Score\":39.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0},{\"ItemCode\":\"800766\",\"QuanTity\":1,\"Price\":3980.00,\"ProductName\":\"佛初草三瓶装\",\"Score\":0.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0}]},{\"OrderID\":\"LD201909192007312257950\",\"ReceiveName\":\"就会v\",\"Mobile\":\"18577653356\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/19 20:07:33\",\"OrderAddress\":\"这些是你想什么\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"西城区\",\"UserID\":\"85445\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"J04N80Z80\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"899386-1\",\"QuanTity\":1,\"Price\":1380.01,\"ProductName\":\"佛初草单瓶装(赠品)\",\"Score\":0.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0}]},{\"OrderID\":\"LD201909192008143139482\",\"ReceiveName\":\"就会v\",\"Mobile\":\"18577653356\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/19 20:08:16\",\"OrderAddress\":\"这些是你想什么\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"西城区\",\"UserID\":\"85445\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"J04N80Z80\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":1185,\"CouponName\":\"全场通用优惠券100元\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"809204\",\"QuanTity\":1,\"Price\":350.00,\"ProductName\":\"浓姜红糖\",\"Score\":0.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0}]},{\"OrderID\":\"LD201909192029463321453\",\"ReceiveName\":\"就会v\",\"Mobile\":\"18577653356\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/19 20:30:11\",\"OrderAddress\":\"这些是你想什么\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"西城区\",\"UserID\":\"85445\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"J04N80Z80\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"809204\",\"QuanTity\":2,\"Price\":350.00,\"ProductName\":\"浓姜红糖\",\"Score\":0.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0}]},{\"OrderID\":\"LD201909192030112518705\",\"ReceiveName\":\"王祥\",\"Mobile\":\"15227904344\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/19 20:30:52\",\"OrderAddress\":\"天通苑北街道东沙各庄\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"昌平区\",\"UserID\":\"96097\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"王祥\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":1234,\"CouponName\":\"全场通用优惠券100元\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"809204\",\"QuanTity\":2,\"Price\":59.01,\"ProductName\":\"浓姜红糖\",\"Score\":39.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0},{\"ItemCode\":\"899569\",\"QuanTity\":2,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0},{\"ItemCode\":\"899570\",\"QuanTity\":2,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0}]},{\"OrderID\":\"LD201909192033291752355\",\"ReceiveName\":\"王祥\",\"Mobile\":\"15227904344\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/19 20:33:32\",\"OrderAddress\":\"天通苑北街道东沙各庄\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"昌平区\",\"UserID\":\"96097\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"王祥\",\"PayName\":\"账户余额\",\"ActivityType\":1,\"ActivityTypeName\":\"拼团活动\",\"ActivityID\":31,\"ActivityName\":\"主+礼拼团购（勿动）\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":1235,\"CouponName\":\"全场通用优惠券100元\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"888890-01\",\"QuanTity\":1,\"Price\":3380.00,\"ProductName\":\"新版净牌-雪莲滋养贴200片\",\"Score\":0.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0},{\"ItemCode\":\"899386-1\",\"QuanTity\":1,\"Price\":1380.00,\"ProductName\":\"佛初草单瓶装(赠品)\",\"Score\":0.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0},{\"ItemCode\":\"899900\",\"QuanTity\":1,\"Price\":200.00,\"ProductName\":\"幸福时代经典炒锅-火象HXG-CG064\",\"Score\":0.00,\"SupplierID\":30,\"SupplierName\":\"北京千城万礼科贸有限公司\",\"ProductType\":0}]},{\"OrderID\":\"LD201909192036599786421\",\"ReceiveName\":\"就会v\",\"Mobile\":\"18577653356\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/19 20:37:02\",\"OrderAddress\":\"这些是你想什么\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"西城区\",\"UserID\":\"85445\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"J04N80Z80\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":1179,\"CouponName\":\"全场通用优惠券100元\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"809204\",\"QuanTity\":1,\"Price\":59.01,\"ProductName\":\"浓姜红糖\",\"Score\":39.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0},{\"ItemCode\":\"899568\",\"QuanTity\":1,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0},{\"ItemCode\":\"899569\",\"QuanTity\":1,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0},{\"ItemCode\":\"899570\",\"QuanTity\":1,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0},{\"ItemCode\":\"899571\",\"QuanTity\":1,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0}]},{\"OrderID\":\"LD201909192037153983206\",\"ReceiveName\":\"123\",\"Mobile\":\"18811484968\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/19 20:37:17\",\"OrderAddress\":\"123456\",\"ProName\":\"四川\",\"CityName\":\"绵阳市\",\"DisName\":\"三台县\",\"UserID\":\"81404\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"灰灰超酷\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"899679-1\",\"QuanTity\":1,\"Price\":0.00,\"ProductName\":\"足癣康\",\"Score\":198.00,\"SupplierID\":1,\"SupplierName\":\"猪猪供货商\",\"ProductType\":0}]},{\"OrderID\":\"LD201909192038059868537\",\"ReceiveName\":\"就会v\",\"Mobile\":\"18577653356\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/19 20:38:06\",\"OrderAddress\":\"这些是你想什么\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"西城区\",\"UserID\":\"85445\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"J04N80Z80\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":1179,\"CouponName\":\"全场通用优惠券100元\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"809204\",\"QuanTity\":1,\"Price\":59.01,\"ProductName\":\"浓姜红糖\",\"Score\":39.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0}]},{\"OrderID\":\"LD201909192038059962152\",\"ReceiveName\":\"就会v\",\"Mobile\":\"18577653356\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/19 20:38:06\",\"OrderAddress\":\"这些是你想什么\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"西城区\",\"UserID\":\"85445\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"J04N80Z80\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":1179,\"CouponName\":\"全场通用优惠券100元\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"899568\",\"QuanTity\":1,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0},{\"ItemCode\":\"899569\",\"QuanTity\":1,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0},{\"ItemCode\":\"899570\",\"QuanTity\":1,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0},{\"ItemCode\":\"899571\",\"QuanTity\":1,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0}]},{\"OrderID\":\"LD201909192042394079730\",\"ReceiveName\":\"王祥\",\"Mobile\":\"15227904344\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/19 20:42:39\",\"OrderAddress\":\"天通苑北街道东沙各庄\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"昌平区\",\"UserID\":\"96097\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"王祥\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":1234,\"CouponName\":\"全场通用优惠券100元\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"809204\",\"QuanTity\":2,\"Price\":59.01,\"ProductName\":\"浓姜红糖\",\"Score\":39.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0}]},{\"OrderID\":\"LD201909192042394171116\",\"ReceiveName\":\"王祥\",\"Mobile\":\"15227904344\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/19 20:42:39\",\"OrderAddress\":\"天通苑北街道东沙各庄\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"昌平区\",\"UserID\":\"96097\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"王祥\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":1234,\"CouponName\":\"全场通用优惠券100元\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"899569\",\"QuanTity\":2,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0},{\"ItemCode\":\"899570\",\"QuanTity\":2,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0}]},{\"OrderID\":\"LD201909192045228624338\",\"ReceiveName\":\"王祥\",\"Mobile\":\"15227904344\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/19 20:45:22\",\"OrderAddress\":\"天通苑北街道东沙各庄\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"昌平区\",\"UserID\":\"96097\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"王祥\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":1235,\"CouponName\":\"全场通用优惠券100元\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"888890-01\",\"QuanTity\":1,\"Price\":3380.00,\"ProductName\":\"新版净牌-雪莲滋养贴200片\",\"Score\":0.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0},{\"ItemCode\":\"899386-1\",\"QuanTity\":1,\"Price\":1380.00,\"ProductName\":\"佛初草单瓶装(赠品)\",\"Score\":0.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0}]},{\"OrderID\":\"LD201909192045228722998\",\"ReceiveName\":\"王祥\",\"Mobile\":\"15227904344\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/19 20:45:22\",\"OrderAddress\":\"天通苑北街道东沙各庄\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"昌平区\",\"UserID\":\"96097\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"王祥\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":1235,\"CouponName\":\"全场通用优惠券100元\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"899900\",\"QuanTity\":1,\"Price\":200.00,\"ProductName\":\"幸福时代经典炒锅-火象HXG-CG064\",\"Score\":0.00,\"SupplierID\":30,\"SupplierName\":\"北京千城万礼科贸有限公司\",\"ProductType\":0}]},{\"OrderID\":\"LD201909200903066314389\",\"ReceiveName\":\"王祥\",\"Mobile\":\"15227904344\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/20 9:03:17\",\"OrderAddress\":\"天通苑北街道东沙各庄\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"昌平区\",\"UserID\":\"96097\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"王祥\",\"PayName\":\"混合支付\",\"ActivityType\":1,\"ActivityTypeName\":\"拼团活动\",\"ActivityID\":31,\"ActivityName\":\"主+礼拼团购（勿动）\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":1204,\"CouponName\":\"全场通用优惠券100元\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"888890-01\",\"QuanTity\":1,\"Price\":3380.00,\"ProductName\":\"新版净牌-雪莲滋养贴200片\",\"Score\":0.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0},{\"ItemCode\":\"899386-1\",\"QuanTity\":1,\"Price\":1380.00,\"ProductName\":\"佛初草单瓶装(赠品)\",\"Score\":0.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0},{\"ItemCode\":\"899900\",\"QuanTity\":1,\"Price\":200.00,\"ProductName\":\"幸福时代经典炒锅-火象HXG-CG064\",\"Score\":0.00,\"SupplierID\":30,\"SupplierName\":\"北京千城万礼科贸有限公司\",\"ProductType\":0}]},{\"OrderID\":\"LD201909200908281918464\",\"ReceiveName\":\"张帅伟\",\"Mobile\":\"13020831303\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/20 9:08:29\",\"OrderAddress\":\"中铁国际城12号楼2单元1703室\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"朝阳区\",\"UserID\":\"77742\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"丑\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"888890-01\",\"QuanTity\":1,\"Price\":3380.00,\"ProductName\":\"新版净牌-雪莲滋养贴200片\",\"Score\":0.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0}]},{\"OrderID\":\"LD201909200909534989894\",\"ReceiveName\":\"王祥\",\"Mobile\":\"15227904344\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/20 9:10:08\",\"OrderAddress\":\"天通苑北街道东沙各庄\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"昌平区\",\"UserID\":\"96097\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"王祥\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":1223,\"CouponName\":\"全场通用优惠券100元\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"899568\",\"QuanTity\":1,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0},{\"ItemCode\":\"899569\",\"QuanTity\":5,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0}]},{\"OrderID\":\"LD201909200912014642184\",\"ReceiveName\":\"王祥\",\"Mobile\":\"15227904344\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/20 9:12:01\",\"OrderAddress\":\"天通苑北街道东沙各庄\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"昌平区\",\"UserID\":\"96097\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"王祥\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":1223,\"CouponName\":\"全场通用优惠券100元\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"899569\",\"QuanTity\":5,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0}]},{\"OrderID\":\"LD201909200912014749671\",\"ReceiveName\":\"王祥\",\"Mobile\":\"15227904344\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/20 9:12:01\",\"OrderAddress\":\"天通苑北街道东沙各庄\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"昌平区\",\"UserID\":\"96097\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"王祥\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":1223,\"CouponName\":\"全场通用优惠券100元\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"899568\",\"QuanTity\":1,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0}]},{\"OrderID\":\"LD201909191809176651683\",\"ReceiveName\":\"呼噜娃\",\"Mobile\":\"15910325153\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/20 9:14:01\",\"OrderAddress\":\"后沙峪三山新新家园30栋6单元501室\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"顺义区\",\"UserID\":\"75828\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":1231,\"CouponName\":\"全场通用优惠券100元\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"899568\",\"QuanTity\":1,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0},{\"ItemCode\":\"899569\",\"QuanTity\":2,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0},{\"ItemCode\":\"899570\",\"QuanTity\":1,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0}]},{\"OrderID\":\"LD201909200921471251885\",\"ReceiveName\":\"王祥\",\"Mobile\":\"15227904344\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/20 9:21:47\",\"OrderAddress\":\"天通苑北街道东沙各庄\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"昌平区\",\"UserID\":\"96097\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"王祥\",\"PayName\":\"混合支付\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":1204,\"CouponName\":\"全场通用优惠券100元\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"888890-01\",\"QuanTity\":1,\"Price\":3380.00,\"ProductName\":\"新版净牌-雪莲滋养贴200片\",\"Score\":0.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0},{\"ItemCode\":\"899386-1\",\"QuanTity\":1,\"Price\":1380.00,\"ProductName\":\"佛初草单瓶装(赠品)\",\"Score\":0.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0}]},{\"OrderID\":\"LD201909200921471356360\",\"ReceiveName\":\"王祥\",\"Mobile\":\"15227904344\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/20 9:21:47\",\"OrderAddress\":\"天通苑北街道东沙各庄\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"昌平区\",\"UserID\":\"96097\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"王祥\",\"PayName\":\"混合支付\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":1204,\"CouponName\":\"全场通用优惠券100元\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"899900\",\"QuanTity\":1,\"Price\":200.00,\"ProductName\":\"幸福时代经典炒锅-火象HXG-CG064\",\"Score\":0.00,\"SupplierID\":30,\"SupplierName\":\"北京千城万礼科贸有限公司\",\"ProductType\":0}]},{\"OrderID\":\"LD201909200924039307258\",\"ReceiveName\":\"呼噜娃\",\"Mobile\":\"15910325153\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/20 9:24:03\",\"OrderAddress\":\"后沙峪三山新新家园30栋6单元501室\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"顺义区\",\"UserID\":\"75828\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"899569\",\"QuanTity\":1,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0}]},{\"OrderID\":\"LD201909200924039401427\",\"ReceiveName\":\"呼噜娃\",\"Mobile\":\"15910325153\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/20 9:24:04\",\"OrderAddress\":\"后沙峪三山新新家园30栋6单元501室\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"顺义区\",\"UserID\":\"75828\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"899568\",\"QuanTity\":1,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0},{\"ItemCode\":\"899570\",\"QuanTity\":1,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0},{\"ItemCode\":\"899571\",\"QuanTity\":1,\"Price\":78.00,\"ProductName\":\"砂山冰感丝袜\",\"Score\":200.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0}]},{\"OrderID\":\"LD201909200927116446667\",\"ReceiveName\":\"王祥\",\"Mobile\":\"15227904344\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/20 9:27:17\",\"OrderAddress\":\"天通苑北街道东沙各庄\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"昌平区\",\"UserID\":\"96097\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"王祥\",\"PayName\":\"账户余额\",\"ActivityType\":1,\"ActivityTypeName\":\"拼团活动\",\"ActivityID\":31,\"ActivityName\":\"主+礼拼团购（勿动）\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":1238,\"CouponName\":\"全场通用优惠券100元\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"888890-01\",\"QuanTity\":1,\"Price\":3380.00,\"ProductName\":\"新版净牌-雪莲滋养贴200片\",\"Score\":0.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0},{\"ItemCode\":\"899386-1\",\"QuanTity\":1,\"Price\":1380.00,\"ProductName\":\"佛初草单瓶装(赠品)\",\"Score\":0.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0},{\"ItemCode\":\"899900\",\"QuanTity\":1,\"Price\":200.00,\"ProductName\":\"幸福时代经典炒锅-火象HXG-CG064\",\"Score\":0.00,\"SupplierID\":30,\"SupplierName\":\"北京千城万礼科贸有限公司\",\"ProductType\":0}]},{\"OrderID\":\"LD201909200928569711385\",\"ReceiveName\":\"张帅伟\",\"Mobile\":\"13020831303\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/20 9:28:58\",\"OrderAddress\":\"中铁国际城12号楼2单元1703室\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"朝阳区\",\"UserID\":\"77742\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"丑\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"899992\",\"QuanTity\":1,\"Price\":0.00,\"ProductName\":\"花王净齿系列超小头标准毛/超小头双列标准毛清洁牙刷\",\"Score\":350.00,\"SupplierID\":28,\"SupplierName\":\"成都龙鹏供应链管理有限公司\",\"ProductType\":0}]},{\"OrderID\":\"LD201909200935485882519\",\"ReceiveName\":\"就会v\",\"Mobile\":\"18577653356\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/20 9:35:52\",\"OrderAddress\":\"这些是你想什么\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"西城区\",\"UserID\":\"85445\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"J04N80Z80\",\"PayName\":\"账户余额\",\"ActivityType\":0,\"ActivityTypeName\":\"\",\"ActivityID\":0,\"ActivityName\":\"\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":0,\"CouponName\":\"\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"809204\",\"QuanTity\":1,\"Price\":59.01,\"ProductName\":\"浓姜红糖\",\"Score\":39.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0}]},{\"OrderID\":\"LD201909200936371061883\",\"ReceiveName\":\"王祥\",\"Mobile\":\"15227904344\",\"CardCode\":\"800099\",\"DocDueDate\":\"2019/9/20 9:36:39\",\"OrderAddress\":\"天通苑北街道东沙各庄\",\"ProName\":\"北京\",\"CityName\":\"市辖区\",\"DisName\":\"昌平区\",\"UserID\":\"96097\",\"Remark\":\"\",\"OrderClass\":0,\"UserName\":\"王祥\",\"PayName\":\"账户余额\",\"ActivityType\":1,\"ActivityTypeName\":\"拼团活动\",\"ActivityID\":31,\"ActivityName\":\"主+礼拼团购（勿动）\",\"CreateUserID\":0,\"CreateUserName\":\"\",\"CouponID\":1236,\"CouponName\":\"全场通用优惠券100元\",\"ShipperType\":0,\"ShipperID\":0,\"ShipperName\":\"\",\"Items\":[{\"ItemCode\":\"888890-01\",\"QuanTity\":1,\"Price\":3380.00,\"ProductName\":\"新版净牌-雪莲滋养贴200片\",\"Score\":0.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0},{\"ItemCode\":\"899386-1\",\"QuanTity\":1,\"Price\":1380.00,\"ProductName\":\"佛初草单瓶装(赠品)\",\"Score\":0.00,\"SupplierID\":0,\"SupplierName\":\"平台供货商\",\"ProductType\":0},{\"ItemCode\":\"899900\",\"QuanTity\":1,\"Price\":200.00,\"ProductName\":\"幸福时代经典炒锅-火象HXG-CG064\",\"Score\":0.00,\"SupplierID\":30,\"SupplierName\":\"北京千城万礼科贸有限公司\",\"ProductType\":0}]}]},\"Msg\":\"成功\"}";
        String json = OrderStatic.lxdpost(OrderStatic.SendOrder, map);
        System.out.println(json);
        OrderJson oj = new OrderJson();
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
                    tk.setProductClass(it.getProductType() + "");
                    tks.add(tk);
                    db = db.add(new BigDecimal(it.getPrice()));
                }

                OrderTask ot = new OrderTask();
                ot.setTaskAmount(db);
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
                ot.setFax(oe.getUserID());
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
            TaskLine tl = TaskLine.dao.findFirst("select id from pool_task_line where task_no=? and product_no=?", transferData.getOrderID(), transferData.getItemCode());
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
            String LogisticsNum = "";
            for (Record es : ess) {
                if (es.getStr("name").contains(cs[0])) {
                    LogisticsNum = es.getStr("remarks");
                    break;
                }
            }
            if (StrKit.isBlank(LogisticsNum)) {
                continue;
            }
            map.put("OrderID", ot.getTaskNo());
            map.put("LogisticsNum", LogisticsNum);
            map.put("LogisticsCode", cs[1]);
            String json = OrderStatic.lxdpost(OrderStatic.SendGoods, map);
            log.info("发送发货" + ot.getTaskNo() + "   " + json);

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
            tk.setProductClass(it.getProductType() + "");
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
        ot.setTaskType(oe.getOrderClass() + "");
        ot.setDmNo(oe.getActivityID() + "");
        ot.setDmName(oe.getActivityName());
        ot.setTaskCreator(oe.getCreateUserName());
        ot.setRemark(oe.getRemark());
        ot.setCreateDate(new Date());
        ot.setShipperName(oe.getShipperName());
        ot.setShipperID(oe.getShipperID() + "");
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
        List<Record> tasklist = Db.find(
                "select DISTINCT pt.id ,pt.task_no" +
                        "from   pool_task pt, pool_task_line ptl, pool_task_line_money  ptlm" +
                        "where     pt.id=ptl.pool_task_id and ptl.id= ptlm.line_id and  ptlm.isok is  null");
        for (Record task : tasklist
        ) {
            List<Record> list = Db.find(
                    "select pt.task_no as platNo,pt.pool_task_no as omsNo,pt.task_type as profitType,ptl.supplierID as shipperId,ptl.supplierName as shipperName," +
                            "ptl.product_class as shipperType , ptl.product_no as itemCode,ptlm.proportion as ratio,ptlm.amount,ptlm.id " +
                            "from   pool_task pt, pool_task_line ptl, pool_task_line_money  ptlm" +
                            "where     pt.id=ptl.pool_task_id and ptl.id= ptlm.line_id and  ptlm.isok is  null and pt.id=?", task.getStr("id"));
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
            r.set("cardCode",task.getStr("cardCode"));
            r.set("cardCode",task.getStr("cardCode"));
            r.set("docDate",task.getDate("BATCH_GEN_DATETIME"));
            r.set("sourceDocEntry",task.getStr("BATCH_NUM"));
            List<Record> salesOrderLines=new ArrayList<>();
            List<Record> salesOrderLines1=Db.find("select * from pool_batch_line where POOL_BATCH_ID=?",task.getStr("id"));

            for (Record r11:salesOrderLines1
                 ) {
                Record r1=new Record();
                r1.set("sourceDocEntry",task.getStr("BATCH_NUM"));
                r1.set("costingCode1","");
                r1.set("costingCode2","");

                r1.set("itemCode",r11.getStr("PRODUCT_ID"));
                r1.set("wharehouse",r11.getStr("whareHouse"));
                r1.set("LineTotal",r11.getBigDecimal("SUM_PRICE"));
                r1.set("quantity",r11.getInt("AMOUNT"));
                salesOrderLines.add(r1);
            }
            r.set("salesOrderLines",salesOrderLines);
            list.add(r);
        }
        String param= JsonKit.toJson(list);
        String json = OrderStatic.post(OrderStatic.salesorder,param);
        log.info("销售订单接口参数"+param);
        ResponseEntity responseEntity = JSON.parseObject(json, ResponseEntity.class);
        log.info("销售订单接口结果"+JsonKit.toJson(responseEntity));

        if (responseEntity.getCode() == 0) {
            try {
                Db.tx(() -> {
                    for (Record r : tasklist
                    ) {
                        String no = responseEntity.getMsg();
                        Db.update("update pool_batch set ERP_NO=? where id=?", no, r.getStr("id"));
                        Db.update("update  pool_task_line   set  erp_no=? where  batch_num=?", no, r.getStr("BATCH_NUM"));
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
    /*
    销售交货接口
     */
    public void sapDelivery() {
        List<Record> tasklist = Db.find("select *  from   pool_batch  where    isok is  null");

        List<Record> list=new ArrayList<>();
        List<Record> list2=new ArrayList<>();
        for (Record task:tasklist) {
            List<Record> os = Db.find("select *  from   pool_task pt,pool_task_line ptl   where   pt.task_status='1' and  pt.id=ptl.pool_task_id and ptl.batch_num=?",task.getStr("BATCH_NUM"));
            if(null!=os&&os.size()>0)
            {
                continue;
            }
            list2.add(task);
            List<Record> salesOrderLines1=Db.find("select * from pool_batch_line where POOL_BATCH_ID=?",task.getStr("id"));

            Record r=new Record();

            r.set("cardCode",task.getStr("cardCode"));
            r.set("docDate",task.getDate("BATCH_GEN_DATETIME"));
            r.set("sourceDocEntry",task.getStr("BATCH_NUM"));

            List<Record> salesOrderLines=new ArrayList<>();

            for (Record r11:salesOrderLines1
            ) {
                Record r1=new Record();
                r1.set("sourceDocEntry",task.getStr("BATCH_NUM"));
                r1.set("costingCode1","");
                r1.set("costingCode2","");

                r1.set("itemCode",r11.getStr("PRODUCT_ID"));
                r1.set("wharehouse",r11.getStr("whareHouse"));
                r1.set("baseEntry",task.getStr("ERP_NO"));
                r1.set("quantity",r11.getInt("AMOUNT"));
                salesOrderLines.add(r1);
            }
            r.set("salesOrderLines",salesOrderLines);
            list.add(r);
        }
        String param=JsonKit.toJson(list);
        String json = OrderStatic.post(OrderStatic.salesdelivery,param );
        log.info("销售交货接口参数"+param);
        ResponseEntity responseEntity = JSON.parseObject(json, ResponseEntity.class);
        log.info("销售交货接口结果"+JsonKit.toJson(responseEntity));

        if (responseEntity.getCode() == 0) {
            try {
                Db.tx(() -> {
                     for (Record r : list2
                    ) {

                        Db.update("update pool_batch set isok=? where id=?", "1", r.getStr("id"));
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
