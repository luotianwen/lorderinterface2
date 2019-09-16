package com.order.www.orderInterface.controller;


import com.alibaba.fastjson.JSON;
import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;
import com.jfinal.kit.Ret;
import com.order.www.orderInterface.KdGoldAPIDemo;
import com.order.www.orderInterface.Test;
import com.order.www.orderInterface.entity.PushData;
import com.order.www.orderInterface.entity.SearchData;
import com.order.www.orderInterface.entity.TransferData;
import com.order.www.orderInterface.service.OrderService;
import top.hequehua.swagger.annotation.*;
import top.hequehua.swagger.config.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**

 */
@Api( tags="order", description = "订单接口")
public class OrderController extends Controller {
	@Inject
	OrderService orderService;
	@ApiOperation(tags="order", methods={RequestMethod.GET,RequestMethod.POST},produces = "application/json",description ="分账信息" )
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

	@ApiOperation(tags="order", methods= RequestMethod.GET,produces = "application/json",description ="打印" )
	/*@ApiParams({
			@ApiParam(name="userName",required=false,description="分账信息")
	})*/
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
	}
	@ApiOperation(tags="order", methods= RequestMethod.GET,produces = "application/json",description ="物流订阅" )
	/*@ApiParams({
			@ApiParam(name="userName",required=false,description="分账信息")
	})*/
	@ApiResponses({
			@ApiResponse(code ="state",message = "状态 ok 为成功")

	})
	/*
	  * 物流信息订阅
	 */
	public void subscript() throws Exception {
		String result=getRawData();
		System.out.println(result);
		SearchData searchData= JSON.parseObject(result, SearchData.class);
		orderService.subscript(searchData);
		renderJson(Ret.ok());
	}
/*	*//**
	 * 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址;
	 *
	 * @return
	 * @throws IOException
	 *//*
	public final static String getIpAddress(HttpServletRequest request) throws IOException {
		// 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址

		String ip = request.getHeader("X-Forwarded-For");

		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("Proxy-Client-IP");
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("WL-Proxy-Client-IP");
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("HTTP_CLIENT_IP");
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("HTTP_X_FORWARDED_FOR");
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getRemoteAddr();
			}
		} else if (ip.length() > 15) {
			String[] ips = ip.split(",");
			for (int index = 0; index < ips.length; index++) {
				String strIp = (String) ips[index];
				if (!("unknown".equalsIgnoreCase(strIp))) {
					ip = strIp;
					break;
				}
			}
		}
		return ip;
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


