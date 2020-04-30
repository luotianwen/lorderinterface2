package com.order.www.orderInterface.entity;

import java.util.List;

public class TransferData {

    /**
     * orderID : LD201904221639537911111
     * amount : 10
     * amountType : 1
     * orderDate : 2019-1-1 00:00:00
     * productName : 佛初草三瓶装
     * productNumber : 1
     * itemCode : 888888
     * createTime : 2019-1-1 00:00:00
     * item : [{"userType":1,"typeName":"平台","amount":100,"proportion":"1 %"},{"userType":2,"typeName":"供应商","amount":5000,"proportion":"50 %"}]
     */

    private String orderID;
    private double amount;
    private int amountType;
    private String orderDate;
    private String productName;
    private int productNumber;
    private String itemCode;
    private String createTime;
    private List<ItemBean> item;

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

    public int getAmountType() {
        return amountType;
    }

    public void setAmountType(int amountType) {
        this.amountType = amountType;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
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

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public List<ItemBean> getItem() {
        return item;
    }

    public void setItem(List<ItemBean> item) {
        this.item = item;
    }

    public static class ItemBean {
        /**
         * userType : 1
         * typeName : 平台
         * amount : 100
         * proportion : 1 %
         */

        private int userType;
        private String typeName;
        private float amount;
        private double proportion;
        private int userID;
        private String name;

        private String  accountName;
        private String accountNumber;
        private String bankName;
        private int amountType;
        private int  transferType;

        public String getAccountName() {
            return accountName;
        }

        public void setAccountName(String accountName) {
            this.accountName = accountName;
        }

        public String getAccountNumber() {
            return accountNumber;
        }

        public void setAccountNumber(String accountNumber) {
            this.accountNumber = accountNumber;
        }

        public String getBankName() {
            return bankName;
        }

        public void setBankName(String bankName) {
            this.bankName = bankName;
        }

        public int getAmountType() {
            return amountType;
        }

        public void setAmountType(int amountType) {
            this.amountType = amountType;
        }

        public int getTransferType() {
            return transferType;
        }

        public void setTransferType(int transferType) {
            this.transferType = transferType;
        }

        public int getUserID() {
            return userID;
        }

        public void setUserID(int userID) {
            this.userID = userID;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getUserType() {
            return userType;
        }

        public void setUserType(int userType) {
            this.userType = userType;
        }

        public String getTypeName() {
            return typeName;
        }

        public void setTypeName(String typeName) {
            this.typeName = typeName;
        }

        public float getAmount() {
            return amount;
        }

        public void setAmount(float amount) {
            this.amount = amount;
        }

        public double getProportion() {
            return proportion;
        }

        public void setProportion(double proportion) {
            this.proportion = proportion;
        }
    }
}
