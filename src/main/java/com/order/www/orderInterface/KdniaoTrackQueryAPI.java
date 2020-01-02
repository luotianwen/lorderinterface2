package com.order.www.orderInterface;

import com.alibaba.fastjson.JSON;
import com.order.www.orderInterface.entity.SubReqData;
import com.order.www.orderInterface.entity.SubReturnData;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * 快递鸟物流轨迹即时查询接口
 *
 * @技术QQ群: 456320272
 * @see: http://www.kdniao.com/YundanChaxunAPI.aspx
 * @copyright: 深圳市快金数据技术服务有限公司
 *
 * DEMO中的电商ID与私钥仅限测试使用，正式环境请单独注册账号
 * 单日超过500单查询量，建议接入我方物流轨迹订阅推送接口
 *
 * ID和Key请到官网申请：http://www.kdniao.com/ServiceApply.aspx
 */

public class KdniaoTrackQueryAPI {

    /**
     * UpdateTime : 2016-08-09 16:42:38
     * Success : true
     * Reason :
     */

    private String UpdateTime;
    private boolean Success;
    private String Reason;

    //DEMO
    public static void main(String[] args) {
       String str="{\n" +
               "        \"EBusinessID\": \"1109259\",\n" +
               "        \"Count\": \"2\",\n" +
               "        \"PushTime\": \"2015-3-11 16:21:06\",\n" +
               "        \"Data\": [\n" +
               "            {\n" +
               "                \"EBusinessID\": \"1109259\",\n" +
               "                \"OrderCode\": \"\",\n" +
               "                \"ShipperCode\": \"EMS\",\n" +
               "                \"LogisticCode\": \"5042260908504\",\n" +
               "                \"Success\": true,\n" +
               "                \"Reason\": \"\",\n" +
               "                \"State\": \"2\",\n" +
               "                \"CallBack\": \"0\",\n" +
               "                \"Traces\": [\n" +
               "                    {\n" +
               "                        \"AcceptTime\": \"2015-03-06 21:16:58\",\n" +
               "                        \"AcceptStation\": \"深圳市横岗速递营销部已收件，（揽投员姓名：钟定基;联系电话：）\",\n" +
               "                        \"Remark\": \"\"\n" +
               "                    },\n" +
               "                    {\n" +
               "                        \"AcceptTime\": \"2015-03-07 14:25:00\",\n" +
               "                        \"AcceptStation\": \"离开深圳市 发往广州市\",\n" +
               "                        \"Remark\": \"\"\n" +
               "                    },\n" +
               "                    {\n" +
               "                        \"AcceptTime\": \"2015-03-08 00:17:00\",\n" +
               "                        \"AcceptStation\": \"到达广东速递物流公司广航中心处理中心（经转）\",\n" +
               "                        \"Remark\": \"\"\n" +
               "                    },\n" +
               "                    {\n" +
               "                        \"AcceptTime\": \"2015-03-08 01:15:00\",\n" +
               "                        \"AcceptStation\": \"离开广州市 发往北京市（经转）\",\n" +
               "                        \"Remark\": \"\"\n" +
               "                    },\n" +
               "                    {\n" +
               "                        \"AcceptTime\": \"2015-03-09 09:01:00\",\n" +
               "                        \"AcceptStation\": \"到达北京黄村转运站处理中心（经转）\",\n" +
               "                        \"Remark\": \"\"\n" +
               "                    },\n" +
               "                    {\n" +
               "                        \"AcceptTime\": \"2015-03-09 18:39:00\",\n" +
               "                        \"AcceptStation\": \"离开北京市 发往呼和浩特市（经转）\",\n" +
               "                        \"Remark\": \"\"\n" +
               "                    },\n" +
               "                    {\n" +
               "                        \"AcceptTime\": \"2015-03-10 18:06:00\",\n" +
               "                        \"AcceptStation\": \"到达  呼和浩特市 处理中心\",\n" +
               "                        \"Remark\": \"\"\n" +
               "                    },\n" +
               "                    {\n" +
               "                        \"AcceptTime\": \"2015-03-11 09:53:48\",\n" +
               "                        \"AcceptStation\": \"呼和浩特市邮政速递物流分公司金川揽投部安排投递（投递员姓名：安长虹;联系电话：18047140142）\",\n" +
               "                        \"Remark\": \"\"\n" +
               "                    }\n" +
               "                ]\n" +
               "            },\n" +
               "            {\n" +
               "                \"EBusinessID\": \"1109259\",\n" +
               "                \"OrderCode\": \"\",\n" +
               "                \"ShipperCode\": \"EMS\",\n" +
               "                \"LogisticCode\": \"5042260943004\",\n" +
               "                \"Success\": true,\n" +
               "                \"Reason\": \"\",\n" +
               "                \"State\": \"2\",\n" +
               "                \"CallBack\": \"0\",\n" +
               "                \"Traces\": [\n" +
               "                    {\n" +
               "                        \"AcceptTime\": \"2015-03-07 15:26:09\",\n" +
               "                        \"AcceptStation\": \"深圳市横岗速递营销部已收件，（揽投员姓名：周宏彪;联系电话：13689537568）\",\n" +
               "                        \"Remark\": \"\"\n" +
               "                    },\n" +
               "                    {\n" +
               "                        \"AcceptTime\": \"2015-03-08 16:32:00\",\n" +
               "                        \"AcceptStation\": \"离开深圳市 发往广州市\",\n" +
               "                        \"Remark\": \"\"\n" +
               "                    },\n" +
               "                    {\n" +
               "                        \"AcceptTime\": \"2015-03-09 00:58:00\",\n" +
               "                        \"AcceptStation\": \"到达广东速递物流公司广航中心处理中心（经转）\",\n" +
               "                        \"Remark\": \"\"\n" +
               "                    },\n" +
               "                    {\n" +
               "                        \"AcceptTime\": \"2015-03-09 01:15:00\",\n" +
               "                        \"AcceptStation\": \"离开广州市 发往北京市（经转）\",\n" +
               "                        \"Remark\": \"\"\n" +
               "                    },\n" +
               "                    {\n" +
               "                        \"AcceptTime\": \"2015-03-10 05:20:00\",\n" +
               "                        \"AcceptStation\": \"到达北京黄村转运站处理中心（经转）\",\n" +
               "                        \"Remark\": \"\"\n" +
               "                    },\n" +
               "                    {\n" +
               "                        \"AcceptTime\": \"2015-03-10 11:59:00\",\n" +
               "                        \"AcceptStation\": \"离开北京市 发往廊坊市（经转）\",\n" +
               "                        \"Remark\": \"\"\n" +
               "                    },\n" +
               "                    {\n" +
               "                        \"AcceptTime\": \"2015-03-10 14:23:00\",\n" +
               "                        \"AcceptStation\": \"到达廊坊市处理中心（经转）\",\n" +
               "                        \"Remark\": \"\"\n" +
               "                    },\n" +
               "                    {\n" +
               "                        \"AcceptTime\": \"2015-03-11 08:55:00\",\n" +
               "                        \"AcceptStation\": \"离开廊坊市 发往保定市（经转）\",\n" +
               "                        \"Remark\": \"\"\n" +
               "                    }\n" +
               "                ]\n" +
               "            }\n" +
               "        ]\n" +
               "    }";

        SubReqData subReqData= JSON.parseObject(str, SubReqData.class);
        SubReturnData subReturnData=new SubReturnData();
        subReturnData.setEbusinessID(subReqData.getEBusinessID());
        subReturnData.setSuccess(true);
        subReturnData.setUpdateTime(subReqData.getPushTime());
        System.out.println(subReqData);
        System.out.println(subReturnData);
    }


    //电商ID
    private String EBusinessID="1568694";
    //电商加密私钥，快递鸟提供，注意保管，不要泄漏
    private String AppKey="6fa5b21e-aa89-4fc1-bbb9-6becd32d7685";
    //请求url
    private String ReqURL="http://api.kdniao.com/Ebusiness/EbusinessOrderHandle.aspx";

    /**
     * Json方式 查询订单物流轨迹
     * @throws Exception
     */
    public String getOrderTracesByJson(String expCode, String expNo) throws Exception{
        String requestData= "{'OrderCode':'','ShipperCode':'" + expCode + "','LogisticCode':'" + expNo + "'}";

        Map<String, String> params = new HashMap<String, String>();
        params.put("RequestData", urlEncoder(requestData, "UTF-8"));
        params.put("EBusinessID", EBusinessID);
        params.put("RequestType", "1002");
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
        String encoded = base64Encode(str.getBytes(charset));
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
                    //System.out.println(entry.getKey()+":"+entry.getValue());
                }
                //System.out.println("param:"+param.toString());
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


    private static char[] base64EncodeChars = new char[] {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
            'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
            'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
            'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',
            'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
            'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
            'w', 'x', 'y', 'z', '0', '1', '2', '3',
            '4', '5', '6', '7', '8', '9', '+', '/' };

    public static String base64Encode(byte[] data) {
        StringBuffer sb = new StringBuffer();
        int len = data.length;
        int i = 0;
        int b1, b2, b3;
        while (i < len) {
            b1 = data[i++] & 0xff;
            if (i == len)
            {
                sb.append(base64EncodeChars[b1 >>> 2]);
                sb.append(base64EncodeChars[(b1 & 0x3) << 4]);
                sb.append("==");
                break;
            }
            b2 = data[i++] & 0xff;
            if (i == len)
            {
                sb.append(base64EncodeChars[b1 >>> 2]);
                sb.append(base64EncodeChars[((b1 & 0x03) << 4) | ((b2 & 0xf0) >>> 4)]);
                sb.append(base64EncodeChars[(b2 & 0x0f) << 2]);
                sb.append("=");
                break;
            }
            b3 = data[i++] & 0xff;
            sb.append(base64EncodeChars[b1 >>> 2]);
            sb.append(base64EncodeChars[((b1 & 0x03) << 4) | ((b2 & 0xf0) >>> 4)]);
            sb.append(base64EncodeChars[((b2 & 0x0f) << 2) | ((b3 & 0xc0) >>> 6)]);
            sb.append(base64EncodeChars[b3 & 0x3f]);
        }
        return sb.toString();
    }

    public String getUpdateTime() {
        return UpdateTime;
    }

    public void setUpdateTime(String UpdateTime) {
        this.UpdateTime = UpdateTime;
    }

    public boolean isSuccess() {
        return Success;
    }

    public void setSuccess(boolean Success) {
        this.Success = Success;
    }

    public String getReason() {
        return Reason;
    }

    public void setReason(String Reason) {
        this.Reason = Reason;
    }
}

