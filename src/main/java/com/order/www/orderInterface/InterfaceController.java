package com.order.www.orderInterface;


import com.jfinal.core.Controller;
import com.jfinal.kit.Ret;
import live.autu.plugin.jfinal.swagger.annotation.Api;
import live.autu.plugin.jfinal.swagger.annotation.ApiImplicitParam;
import live.autu.plugin.jfinal.swagger.annotation.ApiImplicitParams;
import live.autu.plugin.jfinal.swagger.annotation.ApiOperation;
import live.autu.plugin.jfinal.swagger.config.RequestMethod;

/**

 */
@Api( tags="interface", description = "测试")
public class InterfaceController extends Controller {


	@ApiOperation(tags="interface", methods=RequestMethod.GET)
	@ApiImplicitParams({
			@ApiImplicitParam(name="userName",required=false,description="这是学员的姓名")
	})
	public void index() {

		renderJson(Ret.ok("msg","测试成功！").set("userName", "1"));
	}

}


