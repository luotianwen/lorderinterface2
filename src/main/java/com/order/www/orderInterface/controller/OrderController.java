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
import java.util.UUID;

/**

 */
@Api( tags="order", description = "订单接口")
public class OrderController extends Controller {
	@Inject
	OrderService orderService;
	@ApiOperation(tags="GetTransfer", methods= RequestMethod.GET,produces = "application/json")
	@ApiParams({
			@ApiParam(name="userName",required=false,description="分账信息")
	})
	@ApiResponses({
			@ApiResponse(code ="msg",message = "内容")

	})
	public void GetTransfer() throws Exception {
		String result=getRawData();
		System.out.println(result);
		TransferData orderReturn= JSON.parseObject(result, TransferData.class);

		orderService.GetTransfer(orderReturn);
		renderJson(Ret.ok());
	}


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
	/*
	  * 物流信息订阅
	 */
	public void subcript() throws Exception {
		String result=getRawData();
		System.out.println(result);
		PushData orderReturn= JSON.parseObject(result, PushData.class);
		renderJson(orderReturn);
	}
	/**
	 * 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址;
	 *
	 * @param request
	 * @return
	 * @throws IOException
	 */
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
	}
	public void index() {
		String name=getPara("userName");
		renderJson(Ret.ok("msg","测试成功！").set("userName", name));
	}
}


