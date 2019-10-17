package com.order.www.orderInterface.entity;

import java.util.List;

public class StockReData {

    /**
     * code : 0
     * message : OK
     * data : [{"shipperId":"1","shipperName":"ZSPAKJ","shipperType":"3","quantity":null,"wharehouse":null,"itemCode":"899334"},{"shipperId":"1","shipperName":"ZSPAKJ","shipperType":"3","quantity":null,"wharehouse":null,"itemCode":"899334"},{"shipperId":"2","shipperName":"ZSPAHL","shipperType":"3","quantity":null,"wharehouse":null,"itemCode":"899334"},{"shipperId":"2","shipperName":"ZSPAHL","shipperType":"3","quantity":null,"wharehouse":null,"itemCode":"899334"}]
     */

    private String code;
    private String message;
    private List<DataBean> data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * shipperId : 1
         * shipperName : ZSPAKJ
         * shipperType : 3
         * quantity : null
         * wharehouse : null
         * itemCode : 899334
         */

        private String shipperId;
        private String shipperName;
        private String shipperType;
        private int quantity;
        private String wharehouse;
        private String itemCode;

        public String getShipperId() {
            return shipperId;
        }

        public void setShipperId(String shipperId) {
            this.shipperId = shipperId;
        }

        public String getShipperName() {
            return shipperName;
        }

        public void setShipperName(String shipperName) {
            this.shipperName = shipperName;
        }

        public String getShipperType() {
            return shipperType;
        }

        public void setShipperType(String shipperType) {
            this.shipperType = shipperType;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public String getWharehouse() {
            return wharehouse;
        }

        public void setWharehouse(String wharehouse) {
            this.wharehouse = wharehouse;
        }

        public String getItemCode() {
            return itemCode;
        }

        public void setItemCode(String itemCode) {
            this.itemCode = itemCode;
        }
    }
}
