package com.order.www.orderInterface.entity;

import java.util.List;

public class PushData {

    /**
     * pushTime : 2019-09-09 14:31:43
     * ebusinessID : test1568694
     * data : [{"logisticCode":"1234561","shipperCode":"SF","traces":[{"acceptStation":"顺丰速运已收取快件","acceptTime":"2019-09-09 14:31:43","remark":""}],"state":"3","ebusinessID":"test1568694","success":true,"reason":"","callBack":"","estimatedDeliveryTime":"2019-09-09 14:31:43"}]
     * count : 1
     */

    private String pushTime;
    private String ebusinessID;
    private String count;
    private List<DataBean> data;

    public String getPushTime() {
        return pushTime;
    }

    public void setPushTime(String pushTime) {
        this.pushTime = pushTime;
    }

    public String getEbusinessID() {
        return ebusinessID;
    }

    public void setEbusinessID(String ebusinessID) {
        this.ebusinessID = ebusinessID;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * logisticCode : 1234561
         * shipperCode : SF
         * traces : [{"acceptStation":"顺丰速运已收取快件","acceptTime":"2019-09-09 14:31:43","remark":""}]
         * state : 3
         * ebusinessID : test1568694
         * success : true
         * reason :
         * callBack :
         * estimatedDeliveryTime : 2019-09-09 14:31:43
         */

        private String logisticCode;
        private String shipperCode;
        private String state;
        private String ebusinessID;
        private boolean success;
        private String reason;
        private String callBack;
        private String estimatedDeliveryTime;
        private List<TracesBean> traces;

        public String getLogisticCode() {
            return logisticCode;
        }

        public void setLogisticCode(String logisticCode) {
            this.logisticCode = logisticCode;
        }

        public String getShipperCode() {
            return shipperCode;
        }

        public void setShipperCode(String shipperCode) {
            this.shipperCode = shipperCode;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getEbusinessID() {
            return ebusinessID;
        }

        public void setEbusinessID(String ebusinessID) {
            this.ebusinessID = ebusinessID;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public String getCallBack() {
            return callBack;
        }

        public void setCallBack(String callBack) {
            this.callBack = callBack;
        }

        public String getEstimatedDeliveryTime() {
            return estimatedDeliveryTime;
        }

        public void setEstimatedDeliveryTime(String estimatedDeliveryTime) {
            this.estimatedDeliveryTime = estimatedDeliveryTime;
        }

        public List<TracesBean> getTraces() {
            return traces;
        }

        public void setTraces(List<TracesBean> traces) {
            this.traces = traces;
        }

        public static class TracesBean {
            /**
             * acceptStation : 顺丰速运已收取快件
             * acceptTime : 2019-09-09 14:31:43
             * remark :
             */

            private String acceptStation;
            private String acceptTime;
            private String remark;

            public String getAcceptStation() {
                return acceptStation;
            }

            public void setAcceptStation(String acceptStation) {
                this.acceptStation = acceptStation;
            }

            public String getAcceptTime() {
                return acceptTime;
            }

            public void setAcceptTime(String acceptTime) {
                this.acceptTime = acceptTime;
            }

            public String getRemark() {
                return remark;
            }

            public void setRemark(String remark) {
                this.remark = remark;
            }
        }
    }
}
