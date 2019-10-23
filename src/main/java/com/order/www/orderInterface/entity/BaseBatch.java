package com.order.www.orderInterface.entity;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseBatch<M extends BaseBatch<M>> extends Model<M> implements IBean {

	public void setAgentType(java.lang.String id) {
		set("agentType", id);
	}

	public java.lang.String getAgentType() {
		return getStr("agentType");
	}

	public void setSAPSupplierID(java.lang.String id) {
		set("sapSupplierID", id);
	}

	public java.lang.String getSAPSupplierID() {
		return getStr("sapSupplierID");
	}


	public void setId(java.lang.String id) {
		set("id", id);
	}
	
	public java.lang.String getOrderClass() {
		return getStr("orderClass");
	}

	public void setOrderClass(java.lang.String orderClass) {
		set("orderClass", orderClass);
	}

	public java.lang.String getId() {
		return getStr("id");
	}

	public java.lang.String getCardCode() {
		return getStr("cardCode");
	}

	public void setCardCode(java.lang.String shipperID) {
		set("cardCode", shipperID);
	}
	/**
	 * 批次号，订单汇集时生成的编号
	 */
	public void setBatchNum(java.lang.String poolBatch) {
		set("BATCH_NUM", poolBatch);
	}
	
	/**
	 * 批次号，订单汇集时生成的编号
	 */
	public java.lang.String getBatchNum() {
		return getStr("BATCH_NUM");
	}

	/**
	 * 批次生成时间，长日期
	 */
	public void setBatchGenDatetime(java.util.Date batchGenDatetime) {
		set("BATCH_GEN_DATETIME", batchGenDatetime);
	}
	
	/**
	 * 批次生成时间，长日期
	 */
	public java.util.Date getBatchGenDatetime() {
		return get("BATCH_GEN_DATETIME");
	}

	/**
	 * 总金额
	 */
	public void setSumAmt(java.math.BigDecimal sumAmt) {
		set("SUM_AMT", sumAmt);
	}
	
	/**
	 * 总金额
	 */
	public java.math.BigDecimal getSumAmt() {
		return get("SUM_AMT");
	}

	/**
	 * 批次创建人
	 */
	public void setBatchCreator(java.lang.String batchCreator) {
		set("BATCH_CREATOR", batchCreator);
	}
	
	/**
	 * 批次创建人
	 */
	public java.lang.String getBatchCreator() {
		return getStr("BATCH_CREATOR");
	}

	/**
	 * SAP交货单号，写入SAP前为空。
	 */
	public void setErpNo(java.lang.String erpNo) {
		set("ERP_NO", erpNo);
	}
	
	/**
	 * SAP交货单号，写入SAP前为空。
	 */
	public java.lang.String getErpNo() {
		return getStr("ERP_NO");
	}

}
