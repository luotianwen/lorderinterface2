package com.order.www.orderInterface.entity;


import java.io.Serializable;

public class ResponseEntity<T> implements Serializable {

    /**
     * Status : 200
     * Msg : ok
     * Result : {"Total":1,"Surplus":100,"List":[{"OrderID":"LD201904221639537911111","ReceiveName":"test","Mobile":"13888888888","CardCode":"","DocDueDate":"2019-5-6","OrderAddress":"北京市朝阳区某街道某号楼","ProName":"北京市","CityName":"北京市","DisName":"朝阳区","UserID":"12345","Remark":"test","Items":[{"ItemCode":"88888888","QuanTity":1,"Price":10}]}]}
     */

    private int status;
    private String msg;
    private T result;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
