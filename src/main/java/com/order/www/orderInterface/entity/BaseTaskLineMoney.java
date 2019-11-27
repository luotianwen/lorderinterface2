package com.order.www.orderInterface.entity;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseTaskLineMoney<M extends BaseTaskLineMoney<M>> extends Model<M> implements IBean {

	public void setId(java.lang.String id) {
		set("id", id);
	}
	
	public java.lang.String getId() {
		return getStr("id");
	}

	public void setLineId(java.lang.String lineId) {
		set("line_id", lineId);
	}
	
	public java.lang.String getLineId() {
		return getStr("line_id");
	}

	public void setUserType(java.lang.Integer userType) {
		set("userType", userType);
	}
	
	public java.lang.Integer getUserType() {
		return getInt("userType");
	}

	public void setTypeName(java.lang.String typeName) {
		set("typeName", typeName);
	}
	
	public java.lang.String getTypeName() {
		return getStr("typeName");
	}



	public void setUserID(java.lang.Integer userType) {
		set("userID", userType);
	}

	public java.lang.Integer getUserID() {
		return getInt("userID");
	}

	public void setName(java.lang.String typeName) {
		set("name", typeName);
	}

	public java.lang.String getName() {
		return getStr("name");
	}

	/**
	 * 分账金额
	 */
	public void setAmount(java.lang.Float amount) {
		set("amount", amount);
	}
	
	/**
	 * 分账金额
	 */
	public java.lang.Float getAmount() {
		return getFloat("amount");
	}

	/**
	 * 分账比例
	 */
	public void setProportion(java.lang.Double proportion) {
		set("proportion", proportion);
	}
	
	/**
	 * 分账比例
	 */
	public java.lang.Double getProportion() {
		return getDouble("proportion");
	}

}
