package com.order.www.orderInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import java.security.MessageDigest;

/**
 *
 * 快递鸟电子面单接口
 *
 * @技术QQ群: 340378554
 * @see: http://kdniao.com/api-eorder
 * @copyright: 深圳市快金数据技术服务有限公司
 *
 * ID和Key请到官网申请：http://kdniao.com/reg
 */

public class KdGoldAPIDemo {

    //电商ID
    private String EBusinessID="1568694";
    //电商加密私钥，快递鸟提供，注意保管，不要泄漏
    private String AppKey="6fa5b21e-aa89-4fc1-bbb9-6becd32d7685";
    //请求url, 正式环境地址：http://api.kdniao.com/api/Eorderservice    测试环境地址：http://testapi.kdniao.com:8081/api/EOrderService
    private String ReqURL="http://api.kdniao.com/api/Eorderservice";

    public static void main(String[] args) {
        try {
            SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
            String code= sdf.format(new Date());
             System.out.println(new KdGoldAPIDemo().orderOnlineByJson(code));
            String jsonResult = new Test().getPrintParam("192.168.23.23",code);
            System.out.println(jsonResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Json方式 电子面单
     * @throws Exception
     *  <!--圆通快递信息(测试)-->
     *     <!--<add key="clientID" value="K21000119"/>
     *     <add key="partnerID" value="u2Z1F7Fh"/>
     *     <add key="ytoUrl" value="http://58.32.246.71:8000/CommonOrderModeBPlusServlet.action"/>-->
     *     <!--圆通快递信息(生产环境)-->
     *     <add key="clientID" value="K100533161"/>
     *     <add key="partnerID" value="7tqDcFSw"/>
     *     <add key="ytoUrl" value="http://customerewms.yto.net.cn/CommonOrderModeBPlusServlet.action"/>
     *     <!--申通快递信息(测试)-->
     *     <!--<add key="siteName" value="上海陈行公司" />
     *     <add key="customerName" value="李客户1" />
     *     <add key="customerPwd" value="392140" />
     *     <add key="cusOrder" value="http://222.72.44.130:8083/cusOrder" />
     *     <add key="getCusBalance" value="http://222.72.44.130:8085/getCusBalance" />
     *     <add key="checkCusPwd" value="http://222.72.44.130:8085/checkCusPwd" />-->
     *     <!--申通快递信息(生产环境)-->
     *     <add key="siteName" value="北京平谷公司"/>
     *     <add key="customerName" value="莲香岛"/>
     *     <add key="customerPwd" value="575757"/>
     *     <add key="cusOrder" value="http://waybill-get.sto-express.cn:9091/cusOrder"/>
     *     <add key="getCusBalance" value="http://waybill-apply.sto-express.cn:9091"/>
     *     <add key="checkCusPwd" value="http://waybill-apply.sto-express.cn:9091"/>
     */
    public String orderOnlineByJson(String code) throws Exception{
        String scs[]={"STO","YTO","DBL"};
        int i=new Random().nextInt(1);
        String CustomerName="莲香岛";
        String CustomerPwd="575757";
        String MonthCode="";
        String SendSite="北京平谷公司";
        String TemplateSize="180";
        if(i==1){
            CustomerName="K100533161";
            CustomerPwd="7tqDcFSw";
            SendSite="";
            MonthCode="7tqDcFSw";
        }
         else if(i==2){
             CustomerName="testdbl";
            CustomerPwd="";
            SendSite="";
            MonthCode="";
        }
        String requestData= "{'OrderCode':'"+code+"'," +
                "'ShipperCode':'"+scs[i]+"'," +

                "'CustomerName':'"+CustomerName+"'," +
                "'CustomerPwd':'"+CustomerPwd+"'," +
                "'SendSite':'"+SendSite+"'," +
                "'MonthCode':'"+MonthCode+"'," +

                "'PayType':1," +
                "'ExpType':1," +
                "'Cost':1.0," +
                "'OtherCost':1.0," +
                "'Sender':" +
                "{" +
                "'Company':'LV','Name':'Taylor','Mobile':'15018442396','ProvinceName':'上海','CityName':'上海','ExpAreaName':'青浦区','Address':'明珠路73号'}," +
                "'Receiver':" +
                "{" +
                "'Company':'GCCUI','Name':'Yann','Mobile':'15018442396','ProvinceName':'北京','CityName':'北京','ExpAreaName':'朝阳区','Address':'三里屯街道雅秀大厦'}," +
                "'Commodity':" +
                "[{" +
                "'GoodsName':'鞋子','Goodsquantity':1,'GoodsWeight':1.0}]," +
                "'Weight':1.0," +
                "'Quantity':1," +
                "'Volume':0.0," +
                "'TemplateSize':"+TemplateSize+"," +

                "'Remark':'小心轻放'," +
                "'IsReturnPrintTemplate':1}";
        Map<String, String> params = new HashMap<String, String>();
        params.put("RequestData", urlEncoder(requestData, "UTF-8"));
        params.put("EBusinessID", EBusinessID);
        params.put("RequestType", "1007");
        String dataSign=encrypt(requestData, AppKey, "UTF-8");
        params.put("DataSign", urlEncoder(dataSign, "UTF-8"));
        params.put("DataType", "2");

        String result=sendPost(ReqURL, params);

        //根据公司业务处理返回的信息......

        return result;
    }
    /**
     * MD5加密
     * @param str 内容
     * @param charset 编码方式
     * @throws Exception
     */
    @SuppressWarnings("unused")
    private String MD5(String str, String charset) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(str.getBytes(charset));
        byte[] result = md.digest();
        StringBuffer sb = new StringBuffer(32);
        for (int i = 0; i < result.length; i++) {
            int val = result[i] & 0xff;
            if (val <= 0xf) {
                sb.append("0");
            }
            sb.append(Integer.toHexString(val));
        }
        return sb.toString().toLowerCase();
    }

    /**
     * base64编码
     * @param str 内容
     * @param charset 编码方式
     * @throws UnsupportedEncodingException
     */
    private String base64(String str, String charset) throws UnsupportedEncodingException{
        String encoded = Base64.encode(str.getBytes(charset));
        return encoded;
    }

    @SuppressWarnings("unused")
    private String urlEncoder(String str, String charset) throws UnsupportedEncodingException{
        String result = URLEncoder.encode(str, charset);
        return result;
    }

    /**
     * 电商Sign签名生成
     * @param content 内容
     * @param keyValue Appkey
     * @param charset 编码方式
     * @throws UnsupportedEncodingException ,Exception
     * @return DataSign签名
     */
    @SuppressWarnings("unused")
    private String encrypt (String content, String keyValue, String charset) throws UnsupportedEncodingException, Exception
    {
        if (keyValue != null)
        {
            return base64(MD5(content + keyValue, charset), charset);
        }
        return base64(MD5(content, charset), charset);
    }

    /**
     * 向指定 URL 发送POST方法的请求
     * @param url 发送请求的 URL
     * @param params 请求的参数集合
     * @return 远程资源的响应结果
     */
    @SuppressWarnings("unused")
    private String sendPost(String url, Map<String, String> params) {
        OutputStreamWriter out = null;
        BufferedReader in = null;
        StringBuilder result = new StringBuilder();
        try {
            URL realUrl = new URL(url);
            HttpURLConnection conn =(HttpURLConnection) realUrl.openConnection();
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // POST方法
            conn.setRequestMethod("POST");
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.connect();
            // 获取URLConnection对象对应的输出流
            out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            // 发送请求参数
            if (params != null) {
                StringBuilder param = new StringBuilder();
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    if(param.length()>0){
                        param.append("&");
                    }
                    param.append(entry.getKey());
                    param.append("=");
                    param.append(entry.getValue());
                    System.out.println(entry.getKey()+":"+entry.getValue());
                }
                System.out.println("param:"+param.toString());
                out.write(param.toString());
            }
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result.toString();
    }
}
