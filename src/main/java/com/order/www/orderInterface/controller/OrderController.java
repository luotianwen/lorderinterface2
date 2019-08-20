package com.order.www.orderInterface.controller;


import com.jfinal.core.Controller;
import com.jfinal.kit.Ret;
import top.hequehua.swagger.annotation.*;
import top.hequehua.swagger.config.RequestMethod;

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

	public void index() {
         String name=getPara("userName");
		renderJson(Ret.ok("msg","测试成功！").set("userName", name));
	}

}


