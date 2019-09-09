package com.order.www.orderInterface;



import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;


public class Test {
    private static final long serialVersionUID = 1L;

    static final String EBussinessID = "1568694";//kdniao.com EBusinessID
    static final String AppKey = "6fa5b21e-aa89-4fc1-bbb9-6becd32d7685"; //kdniao.com AppKey
    static final Integer IsPreview = 1; //是否预览 0-不预览 1-预览


    public static void main(String[] args) {
        String ip = "192.168.1.1";
        String jsonResult="";
        try {
            jsonResult =new Test().getPrintParam(ip,"sss");
            System.out.println(jsonResult);
        } catch (Exception e) {
            e.printStackTrace();
        }

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
