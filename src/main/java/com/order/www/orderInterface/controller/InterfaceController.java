package com.order.www.orderInterface.controller;


import com.jfinal.core.Controller;
import com.jfinal.kit.Ret;
import top.hequehua.swagger.annotation.Api;
import top.hequehua.swagger.annotation.ApiOperation;
import top.hequehua.swagger.annotation.ApiParam;
import top.hequehua.swagger.annotation.ApiParams;
import top.hequehua.swagger.config.RequestMethod;

/**

 */
@Api( tags="interface", description = "测试")
public class InterfaceController extends Controller {


	@ApiOperation(tags="interface", methods= RequestMethod.GET)
	@ApiParams({
			@ApiParam(name="userName",required=false,description="这是学员的姓名")
	})
	public void index() {

		renderJson(Ret.ok("msg","测试成功！").set("userName", "1"));
	}

}


