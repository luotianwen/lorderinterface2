package com.order.www.orderInterface.entity;

import java.util.List;

public class SearchData {

    /**
     * EBusinessID : 1109259
     * OrderCode :
     * ShipperCode : SF
     * LogisticCode : 118461988807
     * Success : true
     * State : 3
     * Reason : null
     * Traces : [{"AcceptTime":"2014/06/25 08:05:37","AcceptStation":"正在派件..(派件人:邓裕富,电话:18718866310)[深圳 市]","Remark":null},{"AcceptTime":"2014/06/25 04:01:28","AcceptStation":"快件在 深圳集散中心 ,准备送往下一站 深圳 [深圳市]","Remark":null},{"AcceptTime":"2014/06/25 01:41:06","AcceptStation":"快件在 深圳集散中心 [深圳市]","Remark":null},{"AcceptTime":"2014/06/24 20:18:58","AcceptStation":"已收件[深圳市]","Remark":null},{"AcceptTime":"2014/06/24 20:55:28","AcceptStation":"快件在 深圳 ,准备送往下一站 深圳集散中心 [深圳市]","Remark":null},{"AcceptTime":"2014/06/25 10:23:03","AcceptStation":"派件已签收[深圳市]","Remark":null},{"AcceptTime":"2014/06/25 10:23:03","AcceptStation":"签收人是：已签收[深圳市]","Remark":null}]
     */

    private String ebusinessID;
    private String orderCode;
    private String shipperCode;
    private String logisticCode;
    private boolean success;
    private int state;
    private String reason;
    private List<TracesBean> traces;

    public String getEbusinessID() {
        return ebusinessID;
    }

    public void setEbusinessID(String ebusinessID) {
        this.ebusinessID = ebusinessID;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getShipperCode() {
        return shipperCode;
    }

    public void setShipperCode(String shipperCode) {
        this.shipperCode = shipperCode;
    }

    public String getLogisticCode() {
        return logisticCode;
    }

    public void setLogisticCode(String logisticCode) {
        this.logisticCode = logisticCode;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public List<TracesBean> getTraces() {
        return traces;
    }

    public void setTraces(List<TracesBean> traces) {
        this.traces = traces;
    }

    public static class TracesBean {
        /**
         * AcceptTime : 2014/06/25 08:05:37
         * AcceptStation : 正在派件..(派件人:邓裕富,电话:18718866310)[深圳 市]
         * Remark : null
         */

        private String acceptTime;
        private String acceptStation;
        private String remark;

        public String getAcceptTime() {
            return acceptTime;
        }

        public void setAcceptTime(String acceptTime) {
            this.acceptTime = acceptTime;
        }

        public String getAcceptStation() {
            return acceptStation;
        }

        public void setAcceptStation(String acceptStation) {
            this.acceptStation = acceptStation;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }
    }
}
