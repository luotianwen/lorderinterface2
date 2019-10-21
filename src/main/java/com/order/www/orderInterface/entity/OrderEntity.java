package com.order.www.orderInterface.entity;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class OrderEntity implements Serializable {



    @JSONField(format="yyyy/mm/DD HH:mm:ss")
    private Date docDueDate;


    /**
     * OrderID : LD201908291024071292071
     * ReceiveName : 某某某
     * Mobile : 13022225669
     * CardCode : 800099
     * DocDueDate : 2019/8/29 10:24:07
     * OrderAddress : 某个街道的房间
     * ProName : 北京
     * CityName : 市辖区
     * DisName : 朝阳区
     * UserID : 77552
     * Remark :
     * OrderClass : 0
     * UserName : 丑
     * PayName : 账户余额
     * ActivityType : 0
     * ActivityTypeName :
     * ActivityID : 0
     * ActivityName :
     * CreateUserID : 0
     * CreateUserName :
     * CouponID : 0
     * CouponName :
     * ShipperType : 1
     * ShipperID : 1
     * ShipperName : test1
     * Items : [{"ItemCode":"899335","QuanTity":1,"Price":0,"ProductName":"净牌-雪莲滋养贴100片","Score":0,"SupplierID":0,"SupplierName":"众生平安"},{"ItemCode":"888890-01","QuanTity":1,"Price":0,"ProductName":"新版净牌-雪莲滋养贴200片","Score":800,"SupplierID":0,"SupplierName":"众生平安"}]
     */

    private String orderID;
    private String receiveName;
    private String mobile;
    private String cardCode;
    private String orderAddress;
    private String proName;
    private String cityName;
    private String disName;
    private String userID;
    private String remark;
    private int orderClass;
    private String userName;
    private String payName;
    private int activityType;
    private String activityTypeName;
    private int activityID;
    private String activityName;
    private int createUserID;
    private String createUserName;
    private int couponID;
    private String couponName;
    private int shipperType;
    private int shipperID;
    private String shipperName;
    private double  payAmount;
    private double payableAmount;//应付现金
    private double reductionAmount;//减免金额
    private double score;//莲香币
    private String agentType;//代理商标识
    private String sapSupplierID;//SAP供应商ID

    public double getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(double payAmount) {
        this.payAmount = payAmount;
    }

    private List<ItemsBean> Items;
    private int status;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getDocDueDate() {
        return docDueDate;
    }


    public String getOrderAddress() {
        return orderAddress;
    }

    public void setOrderAddress(String orderAddress) {
        this.orderAddress = orderAddress;
    }

    public String getProName() {
        return proName;
    }

    public void setProName(String proName) {
        this.proName = proName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getDisName() {
        return disName;
    }

    public double getPayableAmount() {
        return payableAmount;
    }

    public void setPayableAmount(double payableAmount) {
        this.payableAmount = payableAmount;
    }

    public double getReductionAmount() {
        return reductionAmount;
    }

    public void setReductionAmount(double reductionAmount) {
        this.reductionAmount = reductionAmount;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getAgentType() {
        return agentType;
    }

    public void setAgentType(String agentType) {
        this.agentType = agentType;
    }

    public String getSapSupplierID() {
        return sapSupplierID;
    }

    public void setSapSupplierID(String sapSupplierID) {
        this.sapSupplierID = sapSupplierID;
    }

    public void setDisName(String disName) {
        this.disName = disName;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getOrderClass() {
        return orderClass;
    }

    public void setOrderClass(int orderClass) {
        this.orderClass = orderClass;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPayName() {
        return payName;
    }

    public void setPayName(String payName) {
        this.payName = payName;
    }

    public int getActivityType() {
        return activityType;
    }

    public void setActivityType(int activityType) {
        this.activityType = activityType;
    }

    public String getActivityTypeName() {
        return activityTypeName;
    }

    public void setActivityTypeName(String activityTypeName) {
        this.activityTypeName = activityTypeName;
    }

    public int getActivityID() {
        return activityID;
    }

    public void setActivityID(int activityID) {
        this.activityID = activityID;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public int getCreateUserID() {
        return createUserID;
    }

    public void setCreateUserID(int createUserID) {
        this.createUserID = createUserID;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public int getCouponID() {
        return couponID;
    }

    public void setCouponID(int couponID) {
        this.couponID = couponID;
    }

    public String getCouponName() {
        return couponName;
    }

    public void setCouponName(String couponName) {
        this.couponName = couponName;
    }

    public int getShipperType() {
        return shipperType;
    }

    public void setShipperType(int shipperType) {
        this.shipperType = shipperType;
    }

    public int getShipperID() {
        return shipperID;
    }

    public void setShipperID(int shipperID) {
        this.shipperID = shipperID;
    }

    public String getShipperName() {
        return shipperName;
    }

    public void setShipperName(String shipperName) {
        this.shipperName = shipperName;
    }

    public List<ItemsBean> getItems() {
        return Items;
    }

    public void setItems(List<ItemsBean> items) {
        Items = items;
    }

    public void setDocDueDate(Date docDueDate) {
        this.docDueDate = docDueDate;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getReceiveName() {
        return receiveName;
    }

    public void setReceiveName(String receiveName) {
        this.receiveName = receiveName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCardCode() {
        return cardCode;
    }

    public void setCardCode(String cardCode) {
        this.cardCode = cardCode;
    }

    public static class ItemsBean {
        /**
         * ItemCode : 899335
         * QuanTity : 1
         * Price : 0
         * ProductName : 净牌-雪莲滋养贴100片
         * Score : 0
         * SupplierID : 0
         * SupplierName : 众生平安
         */
        private double payAmount;//商品实付单价
        private double reductionAmount;//减免金额
        private double priceSum;//商品应付总价
        private String agentType;//代理商标识
        private String sapSupplierID;//SAP供应商ID
        private double payAmountSum;//商品实付总价

        private String itemCode;
        private int quanTity;
        private double price;
        private String productName;
        private double score;
        private int supplierID;
        private String supplierName;
        private int productType;

        public double getPayAmount() {
            return payAmount;
        }

        public void setPayAmount(double payAmount) {
            this.payAmount = payAmount;
        }

        public double getReductionAmount() {
            return reductionAmount;
        }

        public void setReductionAmount(double reductionAmount) {
            this.reductionAmount = reductionAmount;
        }

        public double getPriceSum() {
            return priceSum;
        }

        public void setPriceSum(double priceSum) {
            this.priceSum = priceSum;
        }

        public String getAgentType() {
            return agentType;
        }

        public void setAgentType(String agentType) {
            this.agentType = agentType;
        }

        public String getSapSupplierID() {
            return sapSupplierID;
        }

        public void setSapSupplierID(String sapSupplierID) {
            this.sapSupplierID = sapSupplierID;
        }

        public double getPayAmountSum() {
            return payAmountSum;
        }

        public void setPayAmountSum(double payAmountSum) {
            this.payAmountSum = payAmountSum;
        }

        public int getProductType() {
            return productType;
        }

        public void setProductType(int productType) {
            this.productType = productType;
        }

        public String getItemCode() {
            return itemCode;
        }

        public void setItemCode(String itemCode) {
            this.itemCode = itemCode;
        }

        public int getQuanTity() {
            return quanTity;
        }

        public void setQuanTity(int quanTity) {
            this.quanTity = quanTity;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
        }

        public int getSupplierID() {
            return supplierID;
        }

        public void setSupplierID(int supplierID) {
            this.supplierID = supplierID;
        }

        public String getSupplierName() {
            return supplierName;
        }

        public void setSupplierName(String supplierName) {
            this.supplierName = supplierName;
        }
    }
}
