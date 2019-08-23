package com.order.www.orderInterface.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.PropKit;
import com.order.www.orderInterface.entity.OrderBean;
import com.order.www.orderInterface.entity.OrderEntity;
import com.order.www.orderInterface.entity.ResponseEntity;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import sun.security.provider.MD5;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class OrderStatic {
    //向供应商分发订单，由平台提供
    public static final String  SendOrder="http://interface.lxisland.cn/api/Interface/SendOrder";
   //供应商发货后向平台通知发货状态，由平台提供
    public static final String  SendGoods="http://interface.lxisland.cn/api/Interface/SendGoods";
    public static String md5(String plainText){
        byte[] secretBytes = null;
        try {
            secretBytes = MessageDigest.getInstance("md5").digest(
                    plainText.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("没有这个md5算法！");
        }
        String md5code = new BigInteger(1, secretBytes).toString(16);
        for (int i = 0; i < 32 - md5code.length(); i++) {
            md5code = "0" + md5code;
        }

        return md5code;
    }
    /**
     * 发送 post请求访问本地应用并根据传递参数不同返回不同结果
     */
    public static String lxdpost(String url,Map<String, String> params) {

        Map<String, String> headers=new HashMap<>();
        headers.put("From", PropKit.get("lxd.From"));
        //随机数+空格+MD5（MD5（KEY）+随机数）
        int r=new Random().nextInt();
        String keys=PropKit.get("lxd.Key");
        String key=r+" "+ md5(md5(keys).toUpperCase()+r).toUpperCase();
        headers.put("Authorization",key);
        System.out.println(headers.toString());
        return post(url,params,headers);
    }
    /**
     * 发送 post请求访问本地应用并根据传递参数不同返回不同结果
     */
    public static String post(String url,Map<String, String> params,Map<String, String> headers) {
        String str="";
        // 创建默认的httpClient实例.
        CloseableHttpClient httpclient = HttpClients.createDefault();
        // 创建httppost，
        HttpPost httppost = new HttpPost(url);
        Set<String> keys = headers.keySet();
        for (String key : keys) {
            httppost.setHeader(key, headers.get(key).toString());
        }
        // 创建参数队列
        List<BasicNameValuePair> formparams = new ArrayList<BasicNameValuePair>();
        Iterator<String> it = params.keySet().iterator();
        while (it.hasNext()) {
            Object key = it.next();
            formparams.add(new BasicNameValuePair(key.toString(), params.get(key.toString())));
        }


        UrlEncodedFormEntity uefEntity;
        try {
            uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
            httppost.setEntity(uefEntity);
            //System.out.println("executing request " + httppost.getURI());
            CloseableHttpResponse response = httpclient.execute(httppost);
            try {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    str= EntityUtils.toString(entity, "UTF-8");
                    /*System.out.println("--------------------------------------");
                    System.out.println("Response content: " + str);
                    System.out.println("--------------------------------------");*/
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
}
