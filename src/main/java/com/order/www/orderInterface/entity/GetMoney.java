package com.order.www.orderInterface.entity;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;
import java.util.List;

public class GetMoney {

    /**
     * AccountNumber : 62122627130007312345
     * AccountName : 众生平安
     * Amount : 15.0
     * BankName : 中国建设银行
     * UserType : 1
     * UserID : 123
     * TypeName : 代理商返款
     * OrderList : [{"OrderID":"LD201904221639537911111","Amount":10,"OrderDate":"2019-1-1 00:00:00","ProductName":"佛初草三瓶装","ProductNumber":1,"ItemCode":"888888"},{"OrderID":"LD201904221639537911112","Amount":15,"OrderDate":"2019-1-1 00:00:00","ProductName":"佛初草三瓶装","ProductNumber":1,"ItemCode":"888888"}]
     */

    private String accountNumber;
    private String accountName;
    private double amount;
    private String bankName;
    private int userType;
    private int userID;
    private String typeName;
    private List<OrderListBean> orderList;

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public List<OrderListBean> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<OrderListBean> orderList) {
        this.orderList = orderList;
    }

    public static class OrderListBean {
        /**
         * OrderID : LD201904221639537911111
         * Amount : 10.0
         * OrderDate : 2019-1-1 00:00:00
         * ProductName : 佛初草三瓶装
         * ProductNumber : 1
         * ItemCode : 888888
         */

        private String orderID;
        private double amount;
        @JSONField(format="yyyy/mm/DD HH:mm:ss")
        private Date orderDate;
        private String productName;
        private int productNumber;
        private String itemCode;

        public String getOrderID() {
            return orderID;
        }

        public void setOrderID(String orderID) {
            this.orderID = orderID;
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public Date getOrderDate() {
            return orderDate;
        }

        public void setOrderDate(Date orderDate) {
            this.orderDate = orderDate;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public int getProductNumber() {
            return productNumber;
        }

        public void setProductNumber(int productNumber) {
            this.productNumber = productNumber;
        }

        public String getItemCode() {
            return itemCode;
        }

        public void setItemCode(String itemCode) {
            this.itemCode = itemCode;
        }
    }
}
