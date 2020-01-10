package com.order.www.orderInterface;


import com.jfinal.config.*;
import com.jfinal.ext.proxy.CglibProxyFactory;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.cron4j.Cron4jPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.server.undertow.UndertowServer;
import com.jfinal.template.Engine;
import com.order.www.orderInterface.entity._MappingKit;
import com.order.www.orderInterface.routes.FrontApiRoutes;
import top.hequehua.swagger.config.SwaggerPlugin;
import top.hequehua.swagger.handler.WebJarsHandler;
import top.hequehua.swagger.model.SwaggerDoc;

/**
 */
public class DemoConfig extends JFinalConfig {
	
	static Prop p;
	
	/**
	 * 启动入口，运行此 main 方法可以启动项目，此 main 方法可以放置在任意的 Class 类定义中，不一定要放于此
	 */
	public static void main(String[] args) {
		UndertowServer.start(DemoConfig.class);

	}

	@Override
	public void onStart() {

		super.onStart();
		//new GetTransferTask().run();
		 // new OrderCronTask().run();
		  // new OrderBatchTask().run();
		 // new OrderB2BBatchTask().run();
		   // new OrderDeliverTask().run();
		 //new SapDeliveryTask().run();
		  //new SapOrderTask().run();
		  //new SapTask().run();
		 //new SapProfitTask().run();
       /* String result=" {\"Status\":200,\"Result\":[{\"OrderID\":\"LD201910281623031354397\",\"Amount\":3380.0,\"OrderDate\":\"2019/10/28 16:23:03\",\"ProductName\":\"新版净牌-雪莲滋养贴200片\",\"ProductNumber\":1,\"ItemCode\":\"888890-01\",\"CreateTime\":\"2019-10-28 16:23:23\",\"AmountType\":1,\"Item\":[{\"UserType\":5,\"TypeName\":\"魅力合伙人分账\",\"Amount\":338.00,\"Proportion\":0.10},{\"UserType\":0,\"TypeName\":\"门店分账\",\"Amount\":1318.20,\"Proportion\":0.39},{\"UserType\":1,\"TypeName\":\"代理商分账\",\"Amount\":507.00,\"Proportion\":0.15},{\"UserType\":2,\"TypeName\":\"供应商分账\",\"Amount\":338.00,\"Proportion\":0.10},{\"UserType\":3,\"TypeName\":\"平台分账\",\"Amount\":0.00,\"Proportion\":0.00}]}],\"Msg\":\"成功\"}";
		SendsGoodsData orderReturns=JSON.parseObject(result, SendsGoodsData.class);
		//TransferData orderReturn= JSON.parseObject(result, TransferData.class);
		  OrderService orderService = Aop.get(OrderService.class);
		orderService.GetTransfer(orderReturns.getResult());*/
	}

	/**
	 * PropKit.useFirstFound(...) 使用参数中从左到右最先被找到的配置文件
	 * 从左到右依次去找配置，找到则立即加载并立即返回，后续配置将被忽略
	 */
	static void loadConfig() {
		if (p == null) {
			p = PropKit.useFirstFound("demo-config-pro.txt", "demo-config-dev.txt");
		}
	}
	
	/**
	 * 配置常量
	 */
	public void configConstant(Constants me) {
		me.setProxyFactory(new CglibProxyFactory());
		loadConfig();
		
		me.setDevMode(p.getBoolean("devMode", false));
		
		/**
		 * 支持 Controller、Interceptor、Validator 之中使用 @Inject 注入业务层，并且自动实现 AOP
		 * 注入动作支持任意深度并自动处理循环注入
		 */
		me.setInjectDependency(true);
		
		// 配置对超类中的属性进行注入
		me.setInjectSuperClass(true);
	}
	
	/**
	 * 配置路由
	 */
	public void configRoute(Routes me) {
		// me.add(new MySwaggerRoutes());
		 me.add(new FrontApiRoutes());

	}
	
	public void configEngine(Engine me) {

	}
	
	/**
	 * 配置插件
	 */
	public void configPlugin(Plugins me) {
		// 配置 druid 数据库连接池插件
		DruidPlugin druidPlugin = new DruidPlugin(p.get("jdbcUrl"), p.get("user"), p.get("password").trim());
		me.add(druidPlugin);
		// 配置ActiveRecord插件
		ActiveRecordPlugin arp = new ActiveRecordPlugin(druidPlugin);
		me.add(arp);
		_MappingKit.mapping(arp);

		me.add(new SwaggerPlugin(true).addSwaggerDoc(new SwaggerDoc("127.0.0.1:8082","com.order.www.orderInterface.controller","订单集成项目接口文档")));
		Cron4jPlugin cp = new Cron4jPlugin("config.txt", "cron4j");
		me.add(cp);
	}
	
	public static DruidPlugin createDruidPlugin() {
		loadConfig();
		
		return new DruidPlugin(p.get("jdbcUrl"), p.get("user"), p.get("password").trim());
	}
	
	/**
	 * 配置全局拦截器
	 */
	public void configInterceptor(Interceptors me) {
		
	}
	
	/**
	 * 配置处理器
	 */
	public void configHandler(Handlers me) {
		me.add(new WebJarsHandler());
	}
}
