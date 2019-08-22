package com.order.www.orderInterface;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.order.www.orderInterface.entity.OrderBean;
import com.order.www.orderInterface.entity.OrderEntity;
import com.order.www.orderInterface.entity.ResponseEntity;

public class Test {

    public static void main(String[] args) {
        String json="{\n" +
                "\t\"Status\": 200,\n" +
                "\t\"Msg\": \"ok\",\n" +
                "\t\"Result\": {\n" +
                "\t\t\"Total\": 1,\n" +
                "\t\t\"Surplus\": 100,\n" +
                "\t\t\"List\": [{\n" +
                "\t\t\t\"OrderID\": \"LD201904221639537911111\",\n" +
                "\t\t\t\"ReceiveName\": \"test\",\n" +
                "\t\t\t\"Mobile\": \"13888888888\",\n" +
                "\t\t\t\"CardCode\": \"\",\n" +
                "\t\t\t\"DocDueDate\": \"2019-5-6\",\n" +
                "\t\t\t\"OrderAddress\": \"北京市朝阳区某街道某号楼\",\n" +
                "\t\t\t\"ProName\": \"北京市\",\n" +
                "\t\t\t\"CityName\": \"北京市\",\n" +
                "\t\t\t\"DisName\": \"朝阳区\",\n" +
                "\t\t\t\"UserID\": \"12345\",\n" +
                "\t\t\t\"Remark\": \"test\",\n" +
                "\t\t\t\"Items\": [{\n" +
                "\t\t\t\t\"ItemCode\": \"88888888\",\n" +
                "\t\t\t\t\"QuanTity\": 1,\n" +
                "\t\t\t\t\"Price\": 10.00\n" +
                "\t\t\t}]\n" +
                "\t\t}]\n" +
                "\t}\n" +
                "}";

        ResponseEntity<OrderBean> j = JSON.parseObject(json, new TypeReference<ResponseEntity<OrderBean>>(ResponseEntity.class,OrderBean.class,OrderEntity.class){});
        System.out.println(j.getResult().getList().size());

    }

}
