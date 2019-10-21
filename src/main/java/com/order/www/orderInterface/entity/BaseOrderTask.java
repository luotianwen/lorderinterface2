package com.order.www.orderInterface.entity;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseOrderTask<M extends BaseOrderTask<M>> extends Model<M> implements IBean {


	public void setPayableAmount(java.math.BigDecimal payAmount) {
		set("payableAmount", payAmount);
	}
	public java.math.BigDecimal getPayableAmount() {
		return get("payableAmount");
	}
	public void setReductionAmount(java.math.BigDecimal reductionAmount) {
		set("reductionAmount", reductionAmount);
	}
	public java.math.BigDecimal getReductionAmount() {
		return get("reductionAmount");
	}

	public void setScore(java.math.BigDecimal score) {
		set("score", score);
	}
	public java.math.BigDecimal getScore() {	return get("score");	}

	public void setAgentType(java.lang.String id) {
		set("agentType", id);
	}

	public java.lang.String getAgentType() {
		return getStr("agentType");
	}

	public void setSAPSupplierID(java.lang.String id) {
		set("sAPSupplierID", id);
	}

	public java.lang.String getSAPSupplierID() {
		return getStr("sAPSupplierID");
	}







	/**
	 * 订单集成ID，UUID
	 */
	public void setShipperID(java.lang.String shipperID) {
		set("shipperID", shipperID);
	}


	public java.lang.String getCardCode() {
		return getStr("cardCode");
	}

	public void setCardCode(java.lang.String shipperID) {
		set("cardCode", shipperID);
	}
	/**
	 * /**
	 * 订单集成ID，UUID
	 */
	public void setShipperName(java.lang.String id) {
		set("shipperName", id);
	}

	/**
	 * 订单集成ID，UUID
	 */
	public java.lang.String getShipperName() {
		return getStr("shipperName");
	}
	/**
	 */
	/**
	 * 订单集成ID，UUID
	 */
	public void setId(java.lang.String id) {
		set("id", id);
	}
	
	/**
	 * 订单集成ID，UUID
	 */
	public java.lang.String getId() {
		return getStr("id");
	}
	/**
	 * 订单集成ID，UUID
	 */
	public void setRemark(java.lang.String remark) {
		set("remark", remark);
	}

	/**
	 * 订单集成ID，UUID
	 */
	public java.lang.String getRemark() {
		return getStr("remark");
	}

	/**
	 * 订单集成单号
	 */
	public void setPoolTaskNo(java.lang.String poolTaskNo) {
		set("pool_task_no", poolTaskNo);
	}
	
	/**
	 * 订单集成单号
	 */
	public java.lang.String getPoolTaskNo() {
		return getStr("pool_task_no");
	}

	/**
	 * 商城、人客合一、平台订单号
	 */
	public void setTaskNo(java.lang.String taskNo) {
		set("task_no", taskNo);
	}
	
	/**
	 * 商城、人客合一、平台订单号
	 */
	public java.lang.String getTaskNo() {
		return getStr("task_no");
	}

	/**
	 * 供应商订单号，如果是自营商品或供应商无系统，可为空；当前莲香岛科技将所属订单通过邮件以excel格式传给供应商，供应商发货后在excel中补充物流信息后返回，系统需要导入更新物流信息
	 */
	public void setSupplierTaskNo(java.lang.String supplierTaskNo) {
		set("supplier_task_no", supplierTaskNo);
	}
	
	/**
	 * 供应商订单号，如果是自营商品或供应商无系统，可为空；当前莲香岛科技将所属订单通过邮件以excel格式传给供应商，供应商发货后在excel中补充物流信息后返回，系统需要导入更新物流信息
	 */
	public java.lang.String getSupplierTaskNo() {
		return getStr("supplier_task_no");
	}

	/**
	 * 订单产生时间
	 */
	public void setTaskGenDatetime(java.util.Date taskGenDatetime) {
		set("task_gen_datetime", taskGenDatetime);
	}
	
	/**
	 * 订单产生时间
	 */
	public java.util.Date getTaskGenDatetime() {
		return get("task_gen_datetime");
	}

	/**
	 * 付款渠道：微信、支付宝、账户余额等
	 */
	public void setPayWay(java.lang.Integer payWay) {
		set("pay_way", payWay);
	}
	
	/**
	 * 付款渠道：微信、支付宝、账户余额等
	 */
	public java.lang.Integer getPayWay() {
		return getInt("pay_way");
	}

	/**
	 * 订单状态：下单、配货、出库、派送、签收
	 */
	public void setTaskStatus(java.lang.Integer taskStatus) {
		set("task_status", taskStatus);
	}
	
	/**
	 * 订单状态：下单、配货、出库、派送、签收
	 */
	public java.lang.Integer getTaskStatus() {
		return getInt("task_status");
	}
	/**
	 * 状态最后改变时间
	 */
	public void setCreateDate(java.util.Date statusChangeDatetime) {
		set("create_date", statusChangeDatetime);
	}

	/**
	 * 状态最后改变时间
	 */
	public java.util.Date getCreateDate() {
		return get("create_date");
	}
	/**
	 * 状态最后改变时间
	 */
	public void setStatusChangeDatetime(java.util.Date statusChangeDatetime) {
		set("status_change_datetime", statusChangeDatetime);
	}
	
	/**
	 * 状态最后改变时间
	 */
	public java.util.Date getStatusChangeDatetime() {
		return get("status_change_datetime");
	}

	/**
	 * 订单金额
	 */
	public void setTaskAmount(java.math.BigDecimal taskAmount) {
		set("task_amount", taskAmount);
	}
	
	/**
	 * 订单金额
	 */
	public java.math.BigDecimal getTaskAmount() {
		return get("task_amount");
	}

	/**
	 * 发货组织，莲香岛科技、门店或经销商编号
	 */
	public void setSaleGroup(java.lang.String saleGroup) {
		set("sale_group", saleGroup);
	}
	
	/**
	 * 发货组织，莲香岛科技、门店或经销商编号
	 */
	public java.lang.String getSaleGroup() {
		return getStr("sale_group");
	}

	/**
	 * 订单类型：商城、人客合一、平台等，用编号
	 */
	public void setTaskType(java.lang.String taskType) {
		set("task_type", taskType);
	}
	
	/**
	 * 订单类型：商城、人客合一、平台等，用编号
	 */
	public java.lang.String getTaskType() {
		return getStr("task_type");
	}

	/**
	 * 档期编码，如果有促销活动，记录档期编码，有利活动数据分析
	 */
	public void setDmNo(java.lang.String dmNo) {
		set("dm_no", dmNo);
	}
	
	/**
	 * 档期编码，如果有促销活动，记录档期编码，有利活动数据分析
	 */
	public java.lang.String getDmNo() {
		return getStr("dm_no");
	}

	/**
	 * 档期名称
	 */
	public void setDmName(java.lang.String dmName) {
		set("dm_name", dmName);
	}
	
	/**
	 * 档期名称
	 */
	public java.lang.String getDmName() {
		return getStr("dm_name");
	}

	/**
	 * 订单来源，商城、人客合一、APP、微信小程序等，用编号，当前可不要求
	 */
	public void setSource(java.math.BigDecimal source) {
		set("source", source);
	}
	
	/**
	 * 订单来源，商城、人客合一、APP、微信小程序等，用编号，当前可不要求
	 */
	public java.math.BigDecimal getSource() {
		return get("source");
	}

	/**
	 * SAP B1中订单号，写入SAP前为空，通过接口写入SAP时由SAP提供。
	 */
	public void setEbTaskNo(java.lang.String ebTaskNo) {
		set("eb_task_no", ebTaskNo);
	}
	
	/**
	 * SAP B1中订单号，写入SAP前为空，通过接口写入SAP时由SAP提供。
	 */
	public java.lang.String getEbTaskNo() {
		return getStr("eb_task_no");
	}

	/**
	 * SAP交货单号，写入SAP前为空。
	 */
	public void setErpNo(java.lang.String erpNo) {
		set("erp_no", erpNo);
	}
	
	/**
	 * SAP交货单号，写入SAP前为空。
	 */
	public java.lang.String getErpNo() {
		return getStr("erp_no");
	}

	/**
	 * 紧急程度，暂不要求
	 */
	public void setEmergentId(java.lang.String emergentId) {
		set("emergent_id", emergentId);
	}
	
	/**
	 * 紧急程度，暂不要求
	 */
	public java.lang.String getEmergentId() {
		return getStr("emergent_id");
	}

	/**
	 * 失败原因，接口同步信息出错时使用失败原因，接口同步信息出错时使用
	 */
	public void setFailreason(java.lang.String failreason) {
		set("failreason", failreason);
	}
	
	/**
	 * 失败原因，接口同步信息出错时使用失败原因，接口同步信息出错时使用
	 */
	public java.lang.String getFailreason() {
		return getStr("failreason");
	}

	/**
	 * 订单创建人，坐席帮助客户下单时使用
	 */
	public void setTaskCreator(java.lang.String taskCreator) {
		set("task_creator", taskCreator);
	}
	
	/**
	 * 订单创建人，坐席帮助客户下单时使用
	 */
	public java.lang.String getTaskCreator() {
		return getStr("task_creator");
	}

	/**
	 * 客户编号
	 */
	public void setCustomerNo(java.lang.String customerNo) {
		set("customer_no", customerNo);
	}
	
	/**
	 * 客户编号
	 */
	public java.lang.String getCustomerNo() {
		return getStr("customer_no");
	}

	/**
	 * 客户名称
	 */
	public void setCustomerName(java.lang.String customerName) {
		set("customer_name", customerName);
	}
	
	/**
	 * 客户名称
	 */
	public java.lang.String getCustomerName() {
		return getStr("customer_name");
	}

	/**
	 * 客户性别，可不要求
	 */
	public void setSex(java.lang.String sex) {
		set("sex", sex);
	}
	
	/**
	 * 客户性别，可不要求
	 */
	public java.lang.String getSex() {
		return getStr("sex");
	}

	/**
	 * 家庭电话，可不要求
	 */
	public void setHomePhone(java.lang.String homePhone) {
		set("home_phone", homePhone);
	}
	
	/**
	 * 家庭电话，可不要求
	 */
	public java.lang.String getHomePhone() {
		return getStr("home_phone");
	}

	/**
	 * 单位电话，可不要求
	 */
	public void setCompanyPhone(java.lang.String companyPhone) {
		set("company_phone", companyPhone);
	}
	
	/**
	 * 单位电话，可不要求
	 */
	public java.lang.String getCompanyPhone() {
		return getStr("company_phone");
	}

	/**
	 * 客户手机
	 */
	public void setHandPhone(java.lang.String handPhone) {
		set("hand_phone", handPhone);
	}
	
	/**
	 * 客户手机
	 */
	public java.lang.String getHandPhone() {
		return getStr("hand_phone");
	}

	/**
	 * 电子邮件
	 */
	public void setEmail(java.lang.String email) {
		set("email", email);
	}
	
	/**
	 * 电子邮件
	 */
	public java.lang.String getEmail() {
		return getStr("email");
	}

	/**
	 * 会员编号
	 */
	public void setFax(java.lang.String fax) {
		set("fax", fax);
	}
	
	/**
	 * 会员编号
	 */
	public java.lang.String getFax() {
		return getStr("fax");
	}

	/**
	 * 证件名称
	 */
	public void setIdCardName(java.lang.String idCardName) {
		set("id_card_name", idCardName);
	}
	
	/**
	 * 证件名称
	 */
	public java.lang.String getIdCardName() {
		return getStr("id_card_name");
	}

	/**
	 * 证件号码
	 */
	public void setIdCard(java.lang.String idCard) {
		set("id_card", idCard);
	}
	
	/**
	 * 证件号码
	 */
	public java.lang.String getIdCard() {
		return getStr("id_card");
	}

	/**
	 * 收货地址-省
	 */
	public void setAddressProvince(java.lang.String addressProvince) {
		set("address_province", addressProvince);
	}
	
	/**
	 * 收货地址-省
	 */
	public java.lang.String getAddressProvince() {
		return getStr("address_province");
	}

	/**
	 * 收货地址-市
	 */
	public void setAddressCity(java.lang.String addressCity) {
		set("address_city", addressCity);
	}
	
	/**
	 * 收货地址-市
	 */
	public java.lang.String getAddressCity() {
		return getStr("address_city");
	}

	/**
	 * 收货地址-区县
	 */
	public void setAddressCounty(java.lang.String addressCounty) {
		set("address_county", addressCounty);
	}
	
	/**
	 * 收货地址-区县
	 */
	public java.lang.String getAddressCounty() {
		return getStr("address_county");
	}

	/**
	 * 收货详细地址
	 */
	public void setAddressDetail(java.lang.String addressDetail) {
		set("address_detail", addressDetail);
	}
	
	/**
	 * 收货详细地址
	 */
	public java.lang.String getAddressDetail() {
		return getStr("address_detail");
	}

	/**
	 * 邮政编码
	 */
	public void setPostcode(java.lang.String postcode) {
		set("postcode", postcode);
	}
	
	/**
	 * 邮政编码
	 */
	public java.lang.String getPostcode() {
		return getStr("postcode");
	}

	/**
	 * 收货人名称
	 */
	public void setConsigneeName(java.lang.String consigneeName) {
		set("consignee_name", consigneeName);
	}
	
	/**
	 * 收货人名称
	 */
	public java.lang.String getConsigneeName() {
		return getStr("consignee_name");
	}

	/**
	 * 收货人电话
	 */
	public void setConsigneePhone(java.lang.String consigneePhone) {
		set("consignee_phone", consigneePhone);
	}
	
	/**
	 * 收货人电话
	 */
	public java.lang.String getConsigneePhone() {
		return getStr("consignee_phone");
	}

	/**
	 * 发货地址，可用门店编号，如供应商发货可为空
	 */
	public void setPreSendAddress(java.lang.String preSendAddress) {
		set("pre_send_address", preSendAddress);
	}
	
	/**
	 * 发货地址，可用门店编号，如供应商发货可为空
	 */
	public java.lang.String getPreSendAddress() {
		return getStr("pre_send_address");
	}

	/**
	 * 送货方式：自提、快递
	 */
	public void setSendWay(java.lang.String sendWay) {
		set("send_way", sendWay);
	}
	
	/**
	 * 送货方式：自提、快递
	 */
	public java.lang.String getSendWay() {
		return getStr("send_way");
	}

	/**
	 * 承运商，打印运单后确认
	 */
	public void setCarriers(java.lang.String carriers) {
		set("carriers", carriers);
	}
	
	/**
	 * 承运商，打印运单后确认
	 */
	public java.lang.String getCarriers() {
		return getStr("carriers");
	}

	/**
	 * 发票抬头
	 */
	public void setInvoiceTitle(java.lang.String invoiceTitle) {
		set("invoice_title", invoiceTitle);
	}
	
	/**
	 * 发票抬头
	 */
	public java.lang.String getInvoiceTitle() {
		return getStr("invoice_title");
	}

	/**
	 * 发票号
	 */
	public void setInvoiceNo(java.lang.String invoiceNo) {
		set("invoice_no", invoiceNo);
	}
	
	/**
	 * 发票号
	 */
	public java.lang.String getInvoiceNo() {
		return getStr("invoice_no");
	}

	/**
	 * 发票类型
	 */
	public void setInvoiceType(java.lang.String invoiceType) {
		set("invoice_type", invoiceType);
	}
	
	/**
	 * 发票类型
	 */
	public java.lang.String getInvoiceType() {
		return getStr("invoice_type");
	}

	/**
	 * 发票发送方式，如果发票需要单独寄送时考虑使用
	 */
	public void setInvoiceSendId(java.math.BigDecimal invoiceSendId) {
		set("invoice_send_id", invoiceSendId);
	}
	
	/**
	 * 发票发送方式，如果发票需要单独寄送时考虑使用
	 */
	public java.math.BigDecimal getInvoiceSendId() {
		return get("invoice_send_id");
	}

	/**
	 * 发票发送地址
	 */
	public void setInvoiceSendAddress(java.lang.String invoiceSendAddress) {
		set("invoice_send_address", invoiceSendAddress);
	}
	
	/**
	 * 发票发送地址
	 */
	public java.lang.String getInvoiceSendAddress() {
		return getStr("invoice_send_address");
	}

	/**
	 * 发货日期
	 */
	public void setSendStoreDatetime(java.util.Date sendStoreDatetime) {
		set("send_store_datetime", sendStoreDatetime);
	}
	
	/**
	 * 发货日期
	 */
	public java.util.Date getSendStoreDatetime() {
		return get("send_store_datetime");
	}

	/**
	 * 签收标准
	 */
	public void setSignStandard(java.lang.String signStandard) {
		set("sign_standard", signStandard);
	}
	
	/**
	 * 签收标准
	 */
	public java.lang.String getSignStandard() {
		return getStr("sign_standard");
	}

	/**
	 * 是否签收
	 */
	public void setSignResult(java.lang.String signResult) {
		set("sign_result", signResult);
	}
	
	/**
	 * 是否签收
	 */
	public java.lang.String getSignResult() {
		return getStr("sign_result");
	}

	/**
	 * 签收人

	 */
	public void setSignName(java.lang.String signName) {
		set("sign_name", signName);
	}
	
	/**
	 * 签收人

	 */
	public java.lang.String getSignName() {
		return getStr("sign_name");
	}

	/**
	 * 签收时间

	 */
	public void setSignDate(java.util.Date signDate) {
		set("sign_date", signDate);
	}
	
	/**
	 * 签收时间

	 */
	public java.util.Date getSignDate() {
		return get("sign_date");
	}

	/**
	 * 签收后回复确认条件：1、正常签收2、质量有问 
题3、未联系上
	 */
	public void setRecallStatus(java.lang.String recallStatus) {
		set("recall_status", recallStatus);
	}
	
	/**
	 * 签收后回复确认条件：1、正常签收2、质量有问 
题3、未联系上
	 */
	public java.lang.String getRecallStatus() {
		return getStr("recall_status");
	}

}
