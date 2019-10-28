package com.order.www.orderInterface;



import com.jfinal.kit.JsonKit;
import com.jfinal.plugin.activerecord.Record;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class Test {
    private static final long serialVersionUID = 1L;

    static final String EBussinessID = "1568694";//kdniao.com EBusinessID
    static final String AppKey = "6fa5b21e-aa89-4fc1-bbb9-6becd32d7685"; //kdniao.com AppKey
    static final Integer IsPreview = 1; //是否预览 0-不预览 1-预览

    static final String sap = "http://59.110.69.13:9091/";
    public static void main(String[] args) {


    /*    String ip = "192.168.1.1";
        String jsonResult="";
        try {
            jsonResult =new Test().getPrintParam(ip,"sss");
            System.out.println(jsonResult);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
         //journal();
        //salesorder();
        //salesdelivery();
        //stock();

    }
    public static void  stock(){
        String url=sap+"v1/stock";
        url=url+"shipperId=&shipperName=&shipperType=&itemCode=899568";
        System.out.println(url);
        System.out.println(post(url,""));
    }
    public static void  journal(){
        String url=sap+"v1/journal";
        List<Record> ls=new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Record r=new Record();
            r.set("profitType",i);
            r.set("omsNo",i);
            r.set("platNo",i);
            r.set("profitTypeName","sss"+i);
            r.set("shipperId",i);
            r.set("shipperName","aa"+i);
            r.set("shipperType",i);
            r.set("itemCode","7788"+i);
            r.set("ratio",".98");
            r.set("amount","98");
            r.set("rate","");

            ls.add(r);
        }
        System.out.println(url+"   "+JsonKit.toJson(ls));
        System.out.println(post(url, JsonKit.toJson(ls)));
    }
    public static void  salesorder(){
        String url=sap+"v1/salesorder";
        List<Record> ls=new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Record r=new Record();
            r.set("cardCode",i);
            r.set("docDate",new Date());
            r.set("sourceDocEntry",i);
            List<Record> salesOrderLines=new ArrayList<>();
            Record r1=new Record();


            r1.set("sourceDocEntry",i);
            r1.set("costingCode1","");
            r1.set("costingCode2","");

            r1.set("itemCode","7788"+i);
            r1.set("wharehouse","aa");
            r1.set("LineTotal",i);
            r1.set("quantity",i+1);
            salesOrderLines.add(r1);
            r.set("salesOrderLines",salesOrderLines);


            ls.add(r);
        }
        System.out.println(url+"   "+JsonKit.toJson(ls));
        System.out.println(post(url, JsonKit.toJson(ls)));
    }
    public static void  salesdelivery(){
        String url=sap+"v1/salesdelivery";

        List<Record> ls=new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Record r=new Record();
            r.set("cardCode",i);
            r.set("docDate",new Date());
            r.set("sourceDocEntry",i);

            List<Record> salesOrderLines=new ArrayList<>();
            Record r1=new Record();
            r1.set("sourceDocEntry",i);
            r1.set("itemCode","7788"+i);
            r1.set("baseEntry",i);
            r1.set("quantity",i+1);
            salesOrderLines.add(r1);
            r.set("salesOrderLines",salesOrderLines);
            ls.add(r);
        }
        System.out.println(url+"   "+JsonKit.toJson(ls));
        System.out.println(post(url, JsonKit.toJson(ls)));
    }
    /**
     * 发送 post请求访问本地应用并根据传递参数不同返回不同结果
     */
    public static String post(String url, String params) {
        String str="";
        // 创建默认的httpClient实例.
        CloseableHttpClient httpclient = HttpClients.createDefault();
        // 创建httppost，
        HttpPost httppost = new HttpPost(url);
        httppost.addHeader("Content-Type", "application/json");

        // 创建参数队列
        try {
            httppost.setEntity(new StringEntity(params, "UTF-8"));
            CloseableHttpResponse response = httpclient.execute(httppost);
            try {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    str= EntityUtils.toString(entity, "UTF-8");

                }
            } finally {
                response.close();
            }

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭连接,释放资源
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return str;
    }
    /**
     * get print order param to json string
     * @return
     *
     * @throws Exception
     */
    public   String getPrintParam(String ip,String OrderCode) throws Exception {
        String data = "[{\"OrderCode\":"+OrderCode+",\"PortName\":\"电子面单打印机\"}]";
        String result = "{\"RequestData\": \"" + URLEncoder.encode(data, "UTF-8") + "\", \"EBusinessID\":\"" + EBussinessID + "\", \"DataSign\":\"" + encrpy(ip + data, AppKey) + "\", \"IsPreview\":\""
                + IsPreview + "\"}";
        return result;
    }
    private   String md5(String str, String charset) throws Exception {
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

    private   String encrpy(String content, String key) throws UnsupportedEncodingException, Exception {
        String charset = "UTF-8";
        return new String(Base64.encode(md5(content + key, charset).getBytes(charset)));
    }


}
