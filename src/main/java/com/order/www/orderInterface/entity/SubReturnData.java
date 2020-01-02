package com.order.www.orderInterface.entity;

public class SubReturnData {

    /**
     * EBusinessID : 1151847
     * UpdateTime : 2016-08-09 16:42:38
     * Success : true
     * Reason :
     */

    private String ebusinessID;
    private String updateTime;
    private boolean success;
    private String reason;

    public String getEbusinessID() {
        return ebusinessID;
    }

    public void setEbusinessID(String ebusinessID) {
        this.ebusinessID = ebusinessID;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
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
}
