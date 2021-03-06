package com.order.www.orderInterface.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Record;
import com.order.www.orderInterface.common.OrderStatic;
import com.order.www.orderInterface.entity.*;
import com.order.www.orderInterface.service.OrderService;
import top.hequehua.swagger.annotation.*;
import top.hequehua.swagger.config.DataType;
import top.hequehua.swagger.config.RequestMethod;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**

 */
@Api( tags="order", description = "订单接口")
public class OrderController extends Controller {
	Log log = Log.getLog(OrderController.class);
	@Inject
	OrderService orderService;
	@ApiOperation(tags="订单分账", methods={RequestMethod.GET,RequestMethod.POST},produces = "application/json",description ="分账信息" )
	 @ApiParams({
			@ApiParam(required=true, description="[{" +
					" \"OrderID\": \"LD201908291028005125373\"," +
					" \"Amount\": 10.00," +
					" \"AmountType\": 1," +
					" \"OrderDate\": \"2019-1-1 00:00:00\"," +
					" \"ProductName\": \"佛初草三瓶装\"," +
					" \"ProductNumber\": 1," +
					" \"ItemCode\": \"899900\"," +
					" \"CreateTime\": \"2019-1-1 00:00:00\"," +
					" \"Item\": [ " +
					"  \"UserType\": 2," +
					"  \"TypeName\": \"供应商\"," +
					"  \"Amount\": 5000.00," +
					"  \"Proportion\": 0.5," +
					   "UserID\":-1,\"Name\":\"平台\""+
					" }]" +
					"}]")
	})
	@ApiResponses({
			@ApiResponse(code ="state",message = "状态 ok 为成功 其他为 失败")

	})
	public void GetTransfer(){
		String result=getRawData();

		log.info("订单分账参数"+result);
		try {
			List<TransferData> orderReturns=JSON.parseArray(result, TransferData.class);
			//TransferData orderReturn= JSON.parseObject(result, TransferData.class);

			orderService.GetTransfer(orderReturns);
			renderJson(Ret.ok());
		}catch (Exception e){
			log.error(e.getMessage());
			renderJson(Ret.fail());

		}

	}
	@ApiOperation(tags="订单分发接口", methods={RequestMethod.POST},produces = "application/json",description ="订单分发接口信息" )
	@ApiParams({
			@ApiParam(required=true, description="{\n" +
					"                \"OrderID\": \"LD201908291024071292071\",\n" +
					"                \"ReceiveName\": \"某某某\",\n" +
					"                \"Mobile\": \"13022225669\",\n" +
					"                \"CardCode\": \"800099\",\n" +
					"                \"DocDueDate\": \"2019/8/29 10:24:07\",\n" +
					"                \"OrderAddress\": \"某个街道的房间\",\n" +
					"                \"ProName\": \"北京\",\n" +
					"                \"CityName\": \"市辖区\",\n" +
					"                \"DisName\": \"朝阳区\",\n" +
					"                \"UserID\": \"77552\",\n" +
					"                \"Remark\": \"\",\n" +
					"                \"OrderClass\": 0,\n" +
					"                \"UserName\": \"丑\",\n" +
					"                \"PayName\": \"账户余额\",\n" +
					"                \"ActivityType\": 0,\n" +
					"                \"ActivityTypeName\": \"\",\n" +
					"                \"ActivityID\": 0,\n" +
					"                \"ActivityName\": \"\",\n" +
					"                \"CreateUserID\": 0,\n" +
					"                \"CreateUserName\": \"\",\n" +
					"                \"CouponID\": 0,\n" +
					"                \"CouponName\": \"\",\n" +
					"                \"ShipperType\":1,\n" +
					"                \"ShipperID\":1,\n" +
					"                \"ShipperName\":\"test1\"\n" +
					"                \"Items\": [\n" +
					"                    {\n" +
					"                        \"ItemCode\": \"899335\",\n" +
					"                        \"QuanTity\": 1,\n" +
					"                        \"Price\": 0,\n" +
					"                        \"ProductName\": \"净牌-雪莲滋养贴100片\",\n" +
					"                        \"Score\": 0,\n" +
					"                        \"SupplierID\":0,\n" +
					"                        \"SupplierName\":\"众生平安\"\n" +
					"                    },\n" +
					"                    {\n" +
					"                        \"ItemCode\": \"888890-01\",\n" +
					"                        \"QuanTity\": 1,\n" +
					"                        \"Price\": 0,\n" +
					"                        \"ProductName\": \"新版净牌-雪莲滋养贴200片\",\n" +
					"                        \"Score\": 800,\n" +
					"                        \"SupplierID\":0,\n" +
					"                        \"SupplierName\":\"众生平安\"\n" +
					"                    }\n" +
					"                ]\n" +
					"            }")
	})
	@ApiResponses({
			@ApiResponse(code ="state",message = "状态 ok 为成功 其他为 失败")

	})
	public void SendOrder(){
		String result=getRawData();
		System.out.println("订单分发接口参数"+result);
		log.info("订单分发接口参数"+result);
		try {
			OrderEntity orderEntity=JSON.parseObject(result, OrderEntity.class);
			orderService.SendOrder(orderEntity);
			renderJson(Ret.ok());
		}catch (Exception e){
			log.error(e.getMessage());
			renderJson(Ret.fail());
		}

	}

	@ApiOperation(tags="获取打款信息", methods={RequestMethod.GET,RequestMethod.POST},produces = "application/json",description ="获取打款信息接口信息" )
	@ApiParams({
			@ApiParam(required=true, description="{\n" +
					"            \"AccountNumber\":\"62122627130007312345\",\n" +
					"            \"AccountName\":\"众生平安\",\n" +
					"            \"Amount\":15.00,\n" +
					"            \"BankName\":\"中国建设银行\",\n" +
					"            \"UserType\":1,\n" +
					"            \"UserID\":123,\n" +
					"            \"TypeName\":\"代理商返款\",\n" +
					"            \"OrderList\":[\n" +
					"                {\n" +
					"                    \"OrderID\":\"LD201904221639537911111\",\n" +
					"                    \"Amount\":10.00,\n" +
					"                    \"OrderDate\":\"2019-1-1 00:00:00\",\n" +
					"                    \"ProductName\":\"佛初草三瓶装\",\n" +
					"                    \"ProductNumber\":1,\n" +
					"                    \"ItemCode\":\"888888\"\n" +
					"                },\n" +
					"                {\n" +
					"                    \"OrderID\":\"LD201904221639537911112\",\n" +
					"                    \"Amount\":15.00,\n" +
					"                    \"OrderDate\":\"2019-1-1 00:00:00\",\n" +
					"                    \"ProductName\":\"佛初草三瓶装\",\n" +
					"                    \"ProductNumber\":1,\n" +
					"                    \"ItemCode\":\"888888\"\n" +
					"                }\n" +
					"            ]\n" +
					"        }")
	})
	@ApiResponses({
			@ApiResponse(code ="state",message = "状态 ok 为成功 其他为 失败")

	})
	public void GetMoney(){
		String result=getRawData();
		System.out.println("获取打款信息参数"+result);
		log.info("获取打款信息参数"+result);
		try {
			GetMoney money=JSON.parseObject(result, GetMoney.class);
			orderService.getMoney(money);
			renderJson(Ret.ok());
		}catch (Exception e){
			log.error(e.getMessage());
			renderJson(Ret.fail());
		}

	}
	@ApiOperation(tags="批量发货", methods={RequestMethod.GET,RequestMethod.POST},produces = "application/json",description ="批量发货接口信息" )
	@ApiParams({
			@ApiParam(required=true, dataType= DataType.APPLICATION_JSON,paramType="raw", description="[{\"OrderID\":\"LD201904221639537911111\",\"LogisticsNum\":\"物流单号\",\"LogisticsCode\":\"物流公司编码\" } ]" )
	})
	@ApiResponses({
			@ApiResponse(code ="state",message = "状态 ok 为成功 其他为 失败",response = Ret.class)

	})
	public void SendALLGoods(){
		String result=getRawData();

		log.info("批量发货参数"+result);
		try {
			JSONArray ja=JSON.parseArray(result);
			boolean va=false;
			for(int i=0;i<ja.size();i++) {
				String orderID=ja.getJSONObject(i).getString("OrderID");
				String LogisticsNum=ja.getJSONObject(i).getString("LogisticsNum");
				String LogisticsCode=ja.getJSONObject(i).getString("LogisticsCode");
				if(StrKit.isBlank(orderID)||StrKit.isBlank(LogisticsCode)||StrKit.isBlank(LogisticsNum)){
					va=true;
				}
			}
			if(!va) {
				for (int i = 0; i < ja.size(); i++) {
					String orderID=ja.getJSONObject(i).getString("OrderID");
					String LogisticsNum=ja.getJSONObject(i).getString("LogisticsNum");
					String LogisticsCode=ja.getJSONObject(i).getString("LogisticsCode");
					orderService.sendGoods(orderID, LogisticsNum, LogisticsCode);
				}
				renderJson(Ret.ok());
			}
			else{
				renderJson(Ret.fail());
			}


		}catch (Exception e){
			log.error(e.getMessage());
			renderJson(Ret.fail());
		}

	}
	@ApiOperation(tags="订单发货", methods={RequestMethod.GET,RequestMethod.POST},produces = "application/json",description ="订单发货" )
	@ApiParams({
			@ApiParam(required=true, description="  订单号",name = "OrderID"),
			@ApiParam(required =true,description = " 物流单号",name = "LogisticsNum"),
			@ApiParam(required =true,description = " 物流公司编码",name = "LogisticsCode")
	}
	)

	@ApiResponses({
			@ApiResponse(code ="state",message = "状态 ok 为成功 其他为 失败")

	})
	public void SendGoods(){
		String orderID=getPara("OrderID");
		String LogisticsNum=getPara("LogisticsNum");
		String LogisticsCode=getPara("LogisticsCode");
		log.info("订单发货orderID:"+orderID+"单号:"+LogisticsNum+"编码:"+LogisticsCode);
		if(StrKit.isBlank(orderID)||StrKit.isBlank(LogisticsCode)||StrKit.isBlank(LogisticsNum)){
			renderJson(Ret.fail());
		}
		else {
			try {
				orderService.sendGoods(orderID, LogisticsNum, LogisticsCode);
				renderJson(Ret.ok());
			} catch (Exception e) {
				log.error(e.getMessage());
				renderJson(Ret.fail());
			}
		}
	}
	@ApiOperation(tags="订单状态改变", methods={RequestMethod.GET,RequestMethod.POST},produces = "application/json",description ="订单状态改变信息接口" )
	@ApiParams({
			@ApiParam(required=true, description=" {\n" +
					"                    \"OrderID\":\"LD201904221639537911111\",\n" +
					"                    \"Status\":1\n"+
					"        }")
	})
	@ApiResponses({
			@ApiResponse(code ="state",message = "状态 ok 为成功 其他为 失败")

	})
	public void ChangeOrderStatus(){
		String result=getRawData();
		System.out.println("订单状态改变参数"+result);
		log.info("订单状态改变参数"+result);
		try {
			OrderEntity money=JSON.parseObject(result, OrderEntity.class);
			orderService.changeOrderStatus(money);
			renderJson(Ret.ok());
		}catch (Exception e){
			log.error(e.getMessage());
			renderJson(Ret.fail());
		}

	}
	/*@ApiOperation(tags="order", methods= RequestMethod.GET,produces = "application/json",description ="打印" )
	*//*@ApiParams({
			@ApiParam(name="userName",required=false,description="分账信息")
	})*//*
	@ApiResponses({
			@ApiResponse(code ="state",message = "状态 ok 为成功")

	})
	public void print() throws Exception {
		String ip="219.237.112.11";
		//String ip="222.129.19.230";//getIpAddress(getRequest());
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
		String code= "201908"+sdf.format(new Date());//LD20190829 1024 0712 92071
		System.out.println(new KdGoldAPIDemo().orderOnlineByJson(code));
		System.out.println("ip:"+ip);
		String jsonResult = new Test().getPrintParam(ip,code);
		System.out.println(jsonResult);
		renderJson(jsonResult);
	}*/
	 @ApiOperation(tags="物流订阅", methods= RequestMethod.POST,produces = "application/json",description ="物流订阅" )
	 @ApiParams({
			@ApiParam(name="RequestType",required=false,description="101"),
			 @ApiParam(name="RequestData",required=false,description="" +
					 "RequestData={\"PushTime\":\"2019-04-06 13:29:04\",\"EBusinessID\":\"1568694\",\"Data\":[{\"LogisticCode\":\"1234561\",\"ShipperCode\":\"SF\",\"Traces\":[{\"AcceptStation\":\"顺丰速运已收取快件\",\"AcceptTime\":\"2019-04-06 13:29:04\",\"Remark\":\"\"},{\"AcceptStation\":\"货物已经到达深圳\",\"AcceptTime\":\"2019-04-06 13:29:042\",\"Remark\":\"\"},{\"AcceptStation\":\"货物到达福田保税区网点\",\"AcceptTime\":\"2019-04-06 13:29:043\",\"Remark\":\"\"},{\"AcceptStation\":\"货物已经被张三签收了\",\"AcceptTime\":\"2019-04-06 13:29:044\",\"Remark\":\"\"}],\"State\":\"3\",\"EBusinessID\":\"test1261557\",\"Success\":true,\"Reason\":\"\",\"CallBack\":\"\",\"EstimatedDeliveryTime\":\"2019-04-06 13:29:04\"}],\"Count\":\"1\"}&DataType=2&DataSign=1&EBusinessID=1568694RequestType=101&RequestData={\"PushTime\":\"2019-04-06 13:29:04\",\"EBusinessID\":\"1568694\",\"Data\":[{\"LogisticCode\":\"1234561\",\"ShipperCode\":\"SF\",\"Traces\":[{\"AcceptStation\":\"顺丰速运已收取快件\",\"AcceptTime\":\"2019-04-06 13:29:04\",\"Remark\":\"\"},{\"AcceptStation\":\"货物已经到达深圳\",\"AcceptTime\":\"2019-04-06 13:29:042\",\"Remark\":\"\"},{\"AcceptStation\":\"货物到达福田保税区网点\",\"AcceptTime\":\"2019-04-06 13:29:043\",\"Remark\":\"\"},{\"AcceptStation\":\"货物已经被张三签收了\",\"AcceptTime\":\"2019-04-06 13:29:044\",\"Remark\":\"\"}],\"State\":\"3\",\"EBusinessID\":\"test1261557\",\"Success\":true,\"Reason\":\"\",\"CallBack\":\"\",\"EstimatedDeliveryTime\":\"2019-04-06 13:29:04\"}],\"Count\":\"1\"}" +
					 "\t"),
			 @ApiParam(name="DataType",required=false,description="2"),
			 @ApiParam(name="DataSign",required=false,description="1" ),
			 @ApiParam(name="EBusinessID",required=false,description="EBusinessID=1568694")
	})
	@ApiResponses({
			@ApiResponse(code ="state",message = "状态 ok 为成功")

	})
	public void subscript() throws Exception {
	 	String requestData= URLDecoder.decode(getPara("RequestData"),"UTF-8");
		 log.info("RequestData:"+requestData);

		 /* SearchData searchData= JSON.parseObject(requestData, SearchData.class);*/


		 SubReqData subReqData= JSON.parseObject(requestData, SubReqData.class);
		 Record subReturnData=new Record();
		 subReturnData.set("EBusinessID",subReqData.getEBusinessID());
		 subReturnData.set("Success",true);
		 subReturnData.set("Reason","");
		 subReturnData.set("UpdateTime",subReqData.getPushTime());
		 orderService.subscript(subReqData);
		renderJson(subReturnData);
	}


	@ApiOperation(tags="order", methods= RequestMethod.GET,produces = "application/json",description ="首页" )
	/*@ApiParams({
			@ApiParam(name="userName",required=false,description="分账信息")
	})*/
	@ApiResponses({
			@ApiResponse(code ="state",message = "状态 ok 为成功")

	})
	public void index() {
		String name=getPara("userName");
		renderJson(Ret.ok("msg","测试成功！").set("userName", name));
	}
	@ApiOperation(tags="获取商品可用库存", methods= RequestMethod.GET,produces = "application/json",description ="获取商品可用库存" )
	 @ApiParams({
			@ApiParam(name="itemCode",required=false,description="物料号")
	})
	@ApiResponses({
			@ApiResponse(code ="state",message = "状态 ok 为成功")

	})
	public void getStock() {
		String itemCode=getPara("itemCode");
		List<String> ls=new ArrayList();
		ls.add(itemCode);
		String json = OrderStatic.lxdpostJson(OrderStatic.GetItemCodeOccupyStock, JSON.toJSON(ls).toString());
		Detail orderReturn= JSON.parseObject(json, Detail.class);
		int laststock=0;
		Record re=new Record();
		Map map=new HashMap ();
		map.put("Status",0);
		map.put("Msg","成功");
		if(orderReturn.getStatus()==200){
			List<Detail.ResultBean> rbs=orderReturn.getResult();
			for(Detail.ResultBean b:rbs){
				int num=b.getNum();
				re.set("PlatformOccupyStock",num);
				StockReData sd=orderService.getSapStockByItemCode(itemCode.trim());
				int stock=0;
				if(sd!=null&&sd.getData()!=null&&sd.getData().size()>0){
					List<StockReData.DataBean> dbs=sd.getData();
					for (StockReData.DataBean sd1:dbs
					) {
						stock+=sd1.getQuantity();
					}
				}
				re.set("SapStock",stock);
				int omsstock=0;
				Record r= orderService.getOmsStock(itemCode);
				omsstock=r.getInt("omsstock");
				laststock=stock-num-omsstock;
				re.set("OmsOccupyStock",omsstock);
				re.set("Laststock",laststock);
				map.put("Result",re);
			}

		}
		else{
			map.put("Status",1);
			map.put("Msg","失败");
		}

		renderJson(map);
	}
}


