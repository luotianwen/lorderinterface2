package com.order.www.orderInterface.entity;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseTaskLine<M extends BaseTaskLine<M>> extends Model<M> implements IBean {

	public void setPayAmount(java.math.BigDecimal payAmount) {
		set("payAmount", payAmount);
	}
	public java.math.BigDecimal getPayAmount() {
		return get("payAmount");
	}
	public void setReductionAmount(java.math.BigDecimal reductionAmount) {
		set("reductionAmount", reductionAmount);
	}
	public java.math.BigDecimal getReductionAmount() {
		return get("reductionAmount");
	}

	public void setPriceSum(java.math.BigDecimal priceSum) {
		set("priceSum", priceSum);
	}
	public java.math.BigDecimal getPriceSum() {	return get("priceSum");	}
	public void setScore(java.math.BigDecimal score) {
		set("score", score);
	}
	public java.math.BigDecimal getScore() {	return get("score");	}

	public void setWhareHouse(java.lang.String id) {
		set("whareHouse", id);
	}

	public java.lang.String getWhareHouse() {
		return getStr("whareHouse");
	}
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

	public void setPayAmountSum(java.math.BigDecimal payAmountSum) {
		set("payAmountSum", payAmountSum);
	}
	public java.math.BigDecimal getPayAmountSum() {
		return get("payAmountSum");
	}



	public void setAccountNumber(java.lang.String id) {
		set("accountNumber", id);
	}

	public java.lang.String getAccountNumber() {
		return getStr("accountNumber");
	}
	public void setBankName(java.lang.String id) {
		set("bankName", id);
	}

	public java.lang.String getBankName() {
		return getStr("bankName");
	}



	/**
	 * 供应商id
	 */
	public void setSupplierID(int id) {
		set("supplierID", id);
	}

	/**
	 * 供应商id
	 */
	public int getSupplierID() {
		return getInt("supplierID");
	}

	/**
	 * 订单集成行ID,UUID
	 */
	public void setSupplierName(java.lang.String id) {
		set("supplierName", id);
	}

	/**
	 * 订单集成行ID,UUID
	 */
	public java.lang.String getSupplierName() {
		return getStr("supplierName");
	}

	/**
	 * 订单集成行ID,UUID
	 */
	public void setId(java.lang.String id) {
		set("id", id);
	}
	
	/**
	 * 订单集成行ID,UUID
	 */
	public java.lang.String getId() {
		return getStr("id");
	}

	/**
	 * 订单集成ID，UUID
	 */
	public void setPoolTaskId(java.lang.String poolTaskId) {
		set("pool_task_id", poolTaskId);
	}
	public java.lang.String getCardCode() {
		return getStr("cardCode");
	}

	public void setCardCode(java.lang.String shipperID) {
		set("cardCode", shipperID);
	}
	/**
	 * 订单集成ID，UUID
	 */
	public java.lang.String getPoolTaskId() {
		return getStr("pool_task_id");
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
	 * 订单号
	 */
	public void setTaskNo(java.lang.String taskNo) {
		set("task_no", taskNo);
	}
	
	/**
	 * 订单号
	 */
	public java.lang.String getTaskNo() {
		return getStr("task_no");
	}

	/**
	 * 商品名称
	 */
	public void setProductName(java.lang.String productName) {
		set("product_name", productName);
	}
	
	/**
	 * 商品名称
	 */
	public java.lang.String getProductName() {
		return getStr("product_name");
	}

	/**
	 * 商品编号
	 */
	public void setProductNo(java.lang.String productNo) {
		set("product_no", productNo);
	}
	
	/**
	 * 商品编号
	 */
	public java.lang.String getProductNo() {
		return getStr("product_no");
	}

	/**
	 * 产品线/产品分类
	 */
	public void setProductClass(java.lang.String productClass) {
		set("product_class", productClass);
	}
	
	/**
	 * 产品线/产品分类
	 */
	public java.lang.String getProductClass() {
		return getStr("product_class");
	}

	/**
	 * 数量
	 */
	public void setAmount(java.lang.Integer amount) {
		set("amount", amount);
	}
	
	/**
	 * 数量
	 */
	public java.lang.Integer getAmount() {
		return getInt("amount");
	}

	/**
	 * ERP物料编码，订单生产时为空，下单至ERP时回填ERP物料编码，订单生产时为空，下单至ERP时回填
	 */
	public void setProductId(java.lang.String productId) {
		set("product_id", productId);
	}
	
	/**
	 * ERP物料编码，订单生产时为空，下单至ERP时回填ERP物料编码，订单生产时为空，下单至ERP时回填
	 */
	public java.lang.String getProductId() {
		return getStr("product_id");
	}

	/**
	 * 物料名称
	 */
	public void setName(java.lang.String name) {
		set("name", name);
	}
	
	/**
	 * 物料名称
	 */
	public java.lang.String getName() {
		return getStr("name");
	}

	/**
	 * 赠品标识
	 */
	public void setIsPresent(java.lang.String isPresent) {
		set("is_present", isPresent);
	}
	
	/**
	 * 赠品标识
	 */
	public java.lang.String getIsPresent() {
		return getStr("is_present");
	}

	/**
	 * 赠品备注
	 */
	public void setPresentNotes(java.lang.String presentNotes) {
		set("present_notes", presentNotes);
	}
	
	/**
	 * 赠品备注
	 */
	public java.lang.String getPresentNotes() {
		return getStr("present_notes");
	}

	/**
	 * 赠品类型：买赠、促销、赔偿等
	 */
	public void setGiftType(java.lang.String giftType) {
		set("gift_type", giftType);
	}
	
	/**
	 * 赠品类型：买赠、促销、赔偿等
	 */
	public java.lang.String getGiftType() {
		return getStr("gift_type");
	}

	/**
	 * 分期数
	 */
	public void setTimes(java.lang.Integer times) {
		set("times", times);
	}
	
	/**
	 * 分期数
	 */
	public java.lang.Integer getTimes() {
		return getInt("times");
	}

	/**
	 * 分期价格
	 */
	public void setPerTimes(java.math.BigDecimal perTimes) {
		set("per_times", perTimes);
	}
	
	/**
	 * 分期价格
	 */
	public java.math.BigDecimal getPerTimes() {
		return get("per_times");
	}

	/**
	 * 是否原始订单；1：原始订单；0：非原始订单
	 */
	public void setIsOrig(java.lang.String isOrig) {
		set("is_orig", isOrig);
	}
	
	/**
	 * 是否原始订单；1：原始订单；0：非原始订单
	 */
	public java.lang.String getIsOrig() {
		return getStr("is_orig");
	}

	/**
	 * 货币，默认RMB
	 */
	public void setCurrency(java.lang.String currency) {
		set("currency", currency);
	}
	
	/**
	 * 货币，默认RMB
	 */
	public java.lang.String getCurrency() {
		return getStr("currency");
	}

	/**
	 * 分摊价格，如果打折或捆绑销售使用
	 */
	public void setRelievePrice(java.math.BigDecimal relievePrice) {
		set("relieve_price", relievePrice);
	}
	
	/**
	 * 分摊价格，如果打折或捆绑销售使用
	 */
	public java.math.BigDecimal getRelievePrice() {
		return get("relieve_price");
	}

	/**
	 * 莲香币额
	 */
	public void setLxbAmount(java.math.BigDecimal lxbAmount) {
		set("lxb_amount", lxbAmount);
	}
	
	/**
	 * 莲香币额
	 */
	public java.math.BigDecimal getLxbAmount() {
		return get("lxb_amount");
	}

	/**
	 * 莲香币分摊价格
	 */
	public void setLxbPrice(java.math.BigDecimal lxbPrice) {
		set("lxb_price", lxbPrice);
	}
	
	/**
	 * 莲香币分摊价格
	 */
	public java.math.BigDecimal getLxbPrice() {
		return get("lxb_price");
	}

	/**
	 * 金币额
	 */
	public void setGoldenAmount(java.math.BigDecimal goldenAmount) {
		set("golden_amount", goldenAmount);
	}
	
	/**
	 * 金币额
	 */
	public java.math.BigDecimal getGoldenAmount() {
		return get("golden_amount");
	}

	/**
	 * 金币分摊价格
	 */
	public void setGoldenPrice(java.math.BigDecimal goldenPrice) {
		set("golden_price", goldenPrice);
	}
	
	/**
	 * 金币分摊价格
	 */
	public java.math.BigDecimal getGoldenPrice() {
		return get("golden_price");
	}

	/**
	 * 积分额
	 */
	public void setWentAmount(java.math.BigDecimal wentAmount) {
		set("went_amount", wentAmount);
	}
	
	/**
	 * 积分额
	 */
	public java.math.BigDecimal getWentAmount() {
		return get("went_amount");
	}

	/**
	 * 积分分摊价格
	 */
	public void setWentPrice(java.math.BigDecimal wentPrice) {
		set("went_price", wentPrice);
	}
	
	/**
	 * 积分分摊价格
	 */
	public java.math.BigDecimal getWentPrice() {
		return get("went_price");
	}

	/**
	 * 批次号，订单汇集时生成的编号，发货后不可为空
	 */
	public void setBatchNum(java.lang.String batchNum) {
		set("batch_num", batchNum);
	}
	
	/**
	 * 批次号，订单汇集时生成的编号，发货后不可为空
	 */
	public java.lang.String getBatchNum() {
		return getStr("batch_num");
	}

	/**
	 * 备注
	 */
	public void setLineMemo(java.lang.String lineMemo) {
		set("line_memo", lineMemo);
	}
	
	/**
	 * 备注
	 */
	public java.lang.String getLineMemo() {
		return getStr("line_memo");
	}

	/**
	 * 莲香岛科技分润金额
	 */
	public void setProfitLsdtechAmount(java.math.BigDecimal profitLsdtechAmount) {
		set("profit_lsdtech_amount", profitLsdtechAmount);
	}
	
	/**
	 * 莲香岛科技分润金额
	 */
	public java.math.BigDecimal getProfitLsdtechAmount() {
		return get("profit_lsdtech_amount");
	}

	/**
	 * 莲香岛科技分润税率
	 */
	public void setProfitLsdtechRates(java.math.BigDecimal profitLsdtechRates) {
		set("profit_lsdtech_rates", profitLsdtechRates);
	}
	
	/**
	 * 莲香岛科技分润税率
	 */
	public java.math.BigDecimal getProfitLsdtechRates() {
		return get("profit_lsdtech_rates");
	}

	/**
	 * 莲香岛信息技术分润金额
	 */
	public void setProfitLsdinfoAmount(java.math.BigDecimal profitLsdinfoAmount) {
		set("profit_lsdinfo_amount", profitLsdinfoAmount);
	}
	
	/**
	 * 莲香岛信息技术分润金额
	 */
	public java.math.BigDecimal getProfitLsdinfoAmount() {
		return get("profit_lsdinfo_amount");
	}

	/**
	 * 莲香岛信息技术分润税率
	 */
	public void setProfitLsdinfoRates(java.math.BigDecimal profitLsdinfoRates) {
		set("profit_lsdinfo_rates", profitLsdinfoRates);
	}
	
	/**
	 * 莲香岛信息技术分润税率
	 */
	public java.math.BigDecimal getProfitLsdinfoRates() {
		return get("profit_lsdinfo_rates");
	}

	/**
	 * 门店分润金额
	 */
	public void setProfitStoreAmount(java.math.BigDecimal profitStoreAmount) {
		set("profit_store_amount", profitStoreAmount);
	}
	
	/**
	 * 门店分润金额
	 */
	public java.math.BigDecimal getProfitStoreAmount() {
		return get("profit_store_amount");
	}

	/**
	 * 门店分润税率
	 */
	public void setProfitStoreRates(java.math.BigDecimal profitStoreRates) {
		set("profit_store_rates", profitStoreRates);
	}
	
	/**
	 * 门店分润税率
	 */
	public java.math.BigDecimal getProfitStoreRates() {
		return get("profit_store_rates");
	}

	/**
	 * 供应商分润金额
	 */
	public void setProfitSupplierAmount(java.math.BigDecimal profitSupplierAmount) {
		set("profit_supplier_amount", profitSupplierAmount);
	}
	
	/**
	 * 供应商分润金额
	 */
	public java.math.BigDecimal getProfitSupplierAmount() {
		return get("profit_supplier_amount");
	}

	/**
	 * 供应商分润税率
	 */
	public void setProfitSupplierRates(java.math.BigDecimal profitSupplierRates) {
		set("profit_supplier_rates", profitSupplierRates);
	}
	
	/**
	 * 供应商分润税率
	 */
	public java.math.BigDecimal getProfitSupplierRates() {
		return get("profit_supplier_rates");
	}

}
