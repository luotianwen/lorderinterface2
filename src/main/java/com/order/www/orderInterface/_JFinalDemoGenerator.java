package com.order.www.orderInterface;

import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.activerecord.generator.Generator;
import com.jfinal.plugin.druid.DruidPlugin;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * 本 demo 仅表达最为粗浅的 jfinal 用法，更为有价值的实用的企业级用法
 * 详见 JFinal 俱乐部: http://jfinal.com/club
 * 
 * 在数据库表有任何变动时，运行一下 main 方法，极速响应变化进行代码重构
 */
public class _JFinalDemoGenerator {
	
	public static DataSource getDataSource() {
		DruidPlugin druidPlugin = DemoConfig.createDruidPlugin();
		druidPlugin.start();
		return druidPlugin.getDataSource();
	}
	
	public static void main(String[] args) {
		// base model 所使用的包名
		String baseModelPackageName = "com.order.www.orderInterface.entity1";
		// base model 文件保存路径
		String baseModelOutputDir = PathKit.getWebRootPath() + "/src/main/java/com/order/www/orderInterface/entity1";
		
		// model 所使用的包名 (MappingKit 默认使用的包名)
		String modelPackageName = "com.order.www.orderInterface.entity1";
		// model 文件保存路径 (MappingKit 与 DataDictionary 文件默认保存路径)
		String modelOutputDir = baseModelOutputDir + "/../1";
		
		// 创建生成器
		Generator generator = new Generator(getDataSource(), baseModelPackageName, baseModelOutputDir, modelPackageName, modelOutputDir);
		// 配置是否生成备注
		generator.setGenerateRemarks(true);
		
		// 设置数据库方言
		generator.setDialect(new MysqlDialect());
		
		// 设置是否生成链式 setter 方法
		generator.setGenerateChainSetter(false);
		
		// 添加不需要生成的表名
		generator.addExcludedTable(getExcTabs("act_,cms_,sys_,oa_,gen_"));
		// 设置是否在 Model 中生成 dao 对象
		generator.setGenerateDaoInModel(false);
		
		// 设置是否生成字典文件
		generator.setGenerateDataDictionary(false);
		
		// 设置需要被移除的表名前缀用于生成modelName。例如表名 "osc_user"，移除前缀 "osc_"后生成的model名为 "User"而非 OscUser
		generator.setRemovedTableNamePrefixes("pool_");
		
		// 生成
		generator.generate();
	}
	private static String[] getExcTabs(String preName){
		List<String> list=new ArrayList<>();
		String[] al=preName.split(",");
		for (String s:al
			 ) {
			List<String>  a=getExcTab(s);
			list.addAll(a);
		}
		String[] s=new String[list.size()];
		for (int i = 0; i < list.size(); i++) {
			s[i]= list.get(i);
		}
		return s;
	}
	private static List<String> getExcTab(String preName){
		String sql="SELECT table_name from information_schema.tables WHERE table_name LIKE '"+preName+"%'";
		List<String> list = new ArrayList<String>();
		Connection conn = null;
		try {
			conn = getDataSource().getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs=stmt.executeQuery(sql);
			while (rs.next()) {
				list.add(rs.getString(1));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


		return list;
	}
}




