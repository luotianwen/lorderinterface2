package com.order.www.orderInterface.entity;

import java.util.List;

public class SubReqData {

    /**
     * EBusinessID : 1109259
     * Count : 2
     * PushTime : 2015-3-11 16:21:06
     * Data : [{"EBusinessID":"1109259","OrderCode":"","ShipperCode":"EMS","LogisticCode":"5042260908504","Success":true,"Reason":"","State":"2","CallBack":"0","Traces":[{"AcceptTime":"2015-03-06 21:16:58","AcceptStation":"深圳市横岗速递营销部已收件，（揽投员姓名：钟定基;联系电话：）","Remark":""},{"AcceptTime":"2015-03-07 14:25:00","AcceptStation":"离开深圳市 发往广州市","Remark":""},{"AcceptTime":"2015-03-08 00:17:00","AcceptStation":"到达广东速递物流公司广航中心处理中心（经转）","Remark":""},{"AcceptTime":"2015-03-08 01:15:00","AcceptStation":"离开广州市 发往北京市（经转）","Remark":""},{"AcceptTime":"2015-03-09 09:01:00","AcceptStation":"到达北京黄村转运站处理中心（经转）","Remark":""},{"AcceptTime":"2015-03-09 18:39:00","AcceptStation":"离开北京市 发往呼和浩特市（经转）","Remark":""},{"AcceptTime":"2015-03-10 18:06:00","AcceptStation":"到达  呼和浩特市 处理中心","Remark":""},{"AcceptTime":"2015-03-11 09:53:48","AcceptStation":"呼和浩特市邮政速递物流分公司金川揽投部安排投递（投递员姓名：安长虹;联系电话：18047140142）","Remark":""}]},{"EBusinessID":"1109259","OrderCode":"","ShipperCode":"EMS","LogisticCode":"5042260943004","Success":true,"Reason":"","State":"2","CallBack":"0","Traces":[{"AcceptTime":"2015-03-07 15:26:09","AcceptStation":"深圳市横岗速递营销部已收件，（揽投员姓名：周宏彪;联系电话：13689537568）","Remark":""},{"AcceptTime":"2015-03-08 16:32:00","AcceptStation":"离开深圳市 发往广州市","Remark":""},{"AcceptTime":"2015-03-09 00:58:00","AcceptStation":"到达广东速递物流公司广航中心处理中心（经转）","Remark":""},{"AcceptTime":"2015-03-09 01:15:00","AcceptStation":"离开广州市 发往北京市（经转）","Remark":""},{"AcceptTime":"2015-03-10 05:20:00","AcceptStation":"到达北京黄村转运站处理中心（经转）","Remark":""},{"AcceptTime":"2015-03-10 11:59:00","AcceptStation":"离开北京市 发往廊坊市（经转）","Remark":""},{"AcceptTime":"2015-03-10 14:23:00","AcceptStation":"到达廊坊市处理中心（经转）","Remark":""},{"AcceptTime":"2015-03-11 08:55:00","AcceptStation":"离开廊坊市 发往保定市（经转）","Remark":""}]}]
     */

    private String EBusinessID;
    private String Count;
    private String PushTime;
    private List<DataBean> Data;

    public String getEBusinessID() {
        return EBusinessID;
    }

    public void setEBusinessID(String EBusinessID) {
        this.EBusinessID = EBusinessID;
    }

    public String getCount() {
        return Count;
    }

    public void setCount(String Count) {
        this.Count = Count;
    }

    public String getPushTime() {
        return PushTime;
    }

    public void setPushTime(String PushTime) {
        this.PushTime = PushTime;
    }

    public List<DataBean> getData() {
        return Data;
    }

    public void setData(List<DataBean> Data) {
        this.Data = Data;
    }

    public static class DataBean {
        /**
         * EBusinessID : 1109259
         * OrderCode :
         * ShipperCode : EMS
         * LogisticCode : 5042260908504
         * Success : true
         * Reason :
         * State : 2
         * CallBack : 0
         * Traces : [{"AcceptTime":"2015-03-06 21:16:58","AcceptStation":"深圳市横岗速递营销部已收件，（揽投员姓名：钟定基;联系电话：）","Remark":""},{"AcceptTime":"2015-03-07 14:25:00","AcceptStation":"离开深圳市 发往广州市","Remark":""},{"AcceptTime":"2015-03-08 00:17:00","AcceptStation":"到达广东速递物流公司广航中心处理中心（经转）","Remark":""},{"AcceptTime":"2015-03-08 01:15:00","AcceptStation":"离开广州市 发往北京市（经转）","Remark":""},{"AcceptTime":"2015-03-09 09:01:00","AcceptStation":"到达北京黄村转运站处理中心（经转）","Remark":""},{"AcceptTime":"2015-03-09 18:39:00","AcceptStation":"离开北京市 发往呼和浩特市（经转）","Remark":""},{"AcceptTime":"2015-03-10 18:06:00","AcceptStation":"到达  呼和浩特市 处理中心","Remark":""},{"AcceptTime":"2015-03-11 09:53:48","AcceptStation":"呼和浩特市邮政速递物流分公司金川揽投部安排投递（投递员姓名：安长虹;联系电话：18047140142）","Remark":""}]
         */

        private String EBusinessID;
        private String OrderCode;
        private String ShipperCode;
        private String LogisticCode;
        private boolean Success;
        private String Reason;
        private String State;
        private String CallBack;
        private List<TracesBean> Traces;

        public String getEBusinessID() {
            return EBusinessID;
        }

        public void setEBusinessID(String EBusinessID) {
            this.EBusinessID = EBusinessID;
        }

        public String getOrderCode() {
            return OrderCode;
        }

        public void setOrderCode(String OrderCode) {
            this.OrderCode = OrderCode;
        }

        public String getShipperCode() {
            return ShipperCode;
        }

        public void setShipperCode(String ShipperCode) {
            this.ShipperCode = ShipperCode;
        }

        public String getLogisticCode() {
            return LogisticCode;
        }

        public void setLogisticCode(String LogisticCode) {
            this.LogisticCode = LogisticCode;
        }

        public boolean isSuccess() {
            return Success;
        }

        public void setSuccess(boolean Success) {
            this.Success = Success;
        }

        public String getReason() {
            return Reason;
        }

        public void setReason(String Reason) {
            this.Reason = Reason;
        }

        public String getState() {
            return State;
        }

        public void setState(String State) {
            this.State = State;
        }

        public String getCallBack() {
            return CallBack;
        }

        public void setCallBack(String CallBack) {
            this.CallBack = CallBack;
        }

        public List<TracesBean> getTraces() {
            return Traces;
        }

        public void setTraces(List<TracesBean> Traces) {
            this.Traces = Traces;
        }

        public static class TracesBean {
            /**
             * AcceptTime : 2015-03-06 21:16:58
             * AcceptStation : 深圳市横岗速递营销部已收件，（揽投员姓名：钟定基;联系电话：）
             * Remark :
             */

            private String AcceptTime;
            private String AcceptStation;
            private String Remark;

            public String getAcceptTime() {
                return AcceptTime;
            }

            public void setAcceptTime(String AcceptTime) {
                this.AcceptTime = AcceptTime;
            }

            public String getAcceptStation() {
                return AcceptStation;
            }

            public void setAcceptStation(String AcceptStation) {
                this.AcceptStation = AcceptStation;
            }

            public String getRemark() {
                return Remark;
            }

            public void setRemark(String Remark) {
                this.Remark = Remark;
            }
        }
    }
}
