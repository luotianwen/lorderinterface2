package com.order.www.orderInterface.controller;


import com.alibaba.fastjson.JSON;
import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;
import com.jfinal.kit.Ret;
import com.order.www.orderInterface.entity.*;
import com.order.www.orderInterface.service.OrderService;
import top.hequehua.swagger.annotation.*;
import top.hequehua.swagger.config.RequestMethod;

import java.util.List;

/**

 */
@Api( tags="order", description = "订单接口")
public class OrderController extends Controller {
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
					" \"Item\": [{" +
					"  \"UserType\": 1," +
					"  \"TypeName\": \"平台\"," +
					"  \"Amount\": 100.00," +
					"  \"Proportion\": 0.01" +
					" }, {" +
					"  \"UserType\": 2," +
					"  \"TypeName\": \"供应商\"," +
					"  \"Amount\": 5000.00," +
					"  \"Proportion\": 0.5" +
					" }]" +
					"}]")
	})
	@ApiResponses({
			@ApiResponse(code ="state",message = "状态 ok 为成功 其他为 失败")

	})
	public void GetTransfer(){
		String result=getRawData();
		System.out.println(result);
		try {
			List<TransferData> orderReturns=JSON.parseArray(result, TransferData.class);
			//TransferData orderReturn= JSON.parseObject(result, TransferData.class);

			orderService.GetTransfer(orderReturns);
			renderJson(Ret.ok());
		}catch (Exception e){
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
		System.out.println(result);
		try {
			OrderEntity orderEntity=JSON.parseObject(result, OrderEntity.class);
			orderService.SendOrder(orderEntity);
			renderJson(Ret.ok());
		}catch (Exception e){
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
		System.out.println(result);
		try {
			GetMoney money=JSON.parseObject(result, GetMoney.class);
			orderService.getMoney(money);
			renderJson(Ret.ok());
		}catch (Exception e){
			renderJson(Ret.fail());
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
		System.out.println(result);
		try {
			OrderEntity money=JSON.parseObject(result, OrderEntity.class);
			orderService.changeOrderStatus(money);
			renderJson(Ret.ok());
		}catch (Exception e){
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
	/*@ApiOperation(tags="order", methods= RequestMethod.GET,produces = "application/json",description ="物流订阅" )
	*//*@ApiParams({
			@ApiParam(name="userName",required=false,description="分账信息")
	})*//*
	@ApiResponses({
			@ApiResponse(code ="state",message = "状态 ok 为成功")

	})*/
	/*
	  * 物流信息订阅
	 *//*
	public void subscript() throws Exception {
		String result=getRawData();
		System.out.println(result);
		SearchData searchData= JSON.parseObject(result, SearchData.class);
		orderService.subscript(searchData);
		renderJson(Ret.ok());
	}*/


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
}


