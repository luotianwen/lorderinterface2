package com.order.www.orderInterface.controller;


import com.jfinal.core.Controller;
import com.jfinal.kit.Ret;
import com.order.www.orderInterface.KdGoldAPIDemo;
import com.order.www.orderInterface.Test;
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


	@ApiOperation(tags="order", methods= RequestMethod.GET,produces = "application/json")
	@ApiParams({
			@ApiParam(name="userName",required=false,description="测试参数")
	})
	@ApiResponses({
			@ApiResponse(code ="msg",message = "内容"),
			@ApiResponse(code ="userName",message = "名称")
	})

	public void print() throws Exception {
		String ip="219.237.112.11";
		//String ip="222.129.19.230";//getIpAddress(getRequest());
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
		String code= sdf.format(new Date());
		System.out.println(new KdGoldAPIDemo().orderOnlineByJson(code));
		System.out.println("ip:"+ip);
		String jsonResult = new Test().getPrintParam(ip,code);
		System.out.println(jsonResult);
		renderJson(jsonResult);
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


