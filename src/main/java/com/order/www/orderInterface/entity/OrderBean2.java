package com.order.www.orderInterface.entity;

import java.io.Serializable;
import java.util.List;

public class OrderBean2 implements Serializable {
    private int total;
    private int surplus;
    private List<TransferData> list;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getSurplus() {
        return surplus;
    }

    public void setSurplus(int surplus) {
        this.surplus = surplus;
    }

    public List<TransferData> getList() {
        return list;
    }

    public void setList(List<TransferData> list) {
        this.list = list;
    }
}
