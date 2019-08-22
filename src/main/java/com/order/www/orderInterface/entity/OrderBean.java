package com.order.www.orderInterface.entity;

import com.order.www.orderInterface.entity.OrderEntity;

import java.io.Serializable;
import java.util.List;

public class OrderBean implements Serializable {
    private int total;
    private int surplus;
    private java.util.List<OrderEntity> list;

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

    public List<OrderEntity> getList() {
        return list;
    }

    public void setList(List<OrderEntity> list) {
        this.list = list;
    }
}
