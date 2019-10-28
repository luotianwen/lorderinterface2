package com.order.www.orderInterface.entity;

import java.util.List;

public class SendsGoodsData {


    /**
     * Status : 200
     * Result : [{"OrderID":"LD201910281623031354397","Amount":3380,"OrderDate":"2019/10/28 16:23:03","ProductName":"新版净牌-雪莲滋养贴200片","ProductNumber":1,"ItemCode":"888890-01","CreateTime":"2019-10-28 16:23:23","AmountType":1,"Item":[{"UserType":5,"TypeName":"魅力合伙人分账","Amount":338,"Proportion":0.1},{"UserType":0,"TypeName":"门店分账","Amount":1318.2,"Proportion":0.39},{"UserType":1,"TypeName":"代理商分账","Amount":507,"Proportion":0.15},{"UserType":2,"TypeName":"供应商分账","Amount":338,"Proportion":0.1},{"UserType":3,"TypeName":"平台分账","Amount":0,"Proportion":0}]}]
     * Msg : 成功
     */

    private int Status;
    private String Msg;
    private List<TransferData> Result;

    public int getStatus() {
        return Status;
    }

    public void setStatus(int Status) {
        this.Status = Status;
    }

    public String getMsg() {
        return Msg;
    }

    public void setMsg(String Msg) {
        this.Msg = Msg;
    }

    public List<TransferData> getResult() {
        return Result;
    }

    public void setResult(List<TransferData> Result) {
        this.Result = Result;
    }

}
