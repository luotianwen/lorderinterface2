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
import com.order.www.orderInterface.task.OrderCronTask;
import top.hequehua.swagger.config.SwaggerPlugin;
import top.hequehua.swagger.handler.WebJarsHandler;
import top.hequehua.swagger.model.SwaggerDoc;
import top.hequehua.swagger.routes.MySwaggerRoutes;

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
       // new OrderBatchTask().run();
		 new OrderCronTask().run();
		//new OrderB2CBatchTask().run();
		//new OrderB2BBatchTask().run();
		//new OrderDeliverTask().run();
		 //new SapDeliveryTask().run();
		//new SapOrderTask().run();
		//new SapProfitTask().run();
     /*   String result="[{\"OrderID\":\"LD201910181422572826938\",\"Amount\":0.0,\"AmountType\":1,\"OrderDate\":\"2019-10-18T14:22:57\",\"ProductName\":\"佛初草三瓶装\",\"ProductNumber\":10,\"ItemCode\":\"800766\",\"CreateTime\":\"2019-10-18T14:25:50\",\"Item\":[{\"UserType\":0,\"TypeName\":\"门店分账\",\"Amount\":0.00,\"Proportion\":0.00},{\"UserType\":1,\"TypeName\":\"代理商分账\",\"Amount\":10940.00,\"Proportion\":0.60},{\"UserType\":2,\"TypeName\":\"供应商分账\",\"Amount\":7562.00,\"Proportion\":0.38},{\"UserType\":3,\"TypeName\":\"平台分账\",\"Amount\":199.00,\"Proportion\":0.01}]}]";
		List<TransferData> orderReturns= JSON.parseArray(result, TransferData.class);
		//TransferData orderReturn= JSON.parseObject(result, TransferData.class);
		  OrderService orderService = Aop.get(OrderService.class);
		orderService.GetTransfer(orderReturns);*/
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
		  me.add(new MySwaggerRoutes());
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
