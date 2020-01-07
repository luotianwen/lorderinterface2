package com.order.www.orderInterface.entity;

import java.util.List;

/**
 * Generated by JFinal.
 */
@SuppressWarnings("serial")
public class TaskLine extends BaseTaskLine<TaskLine> {
    public static final TaskLine dao = new TaskLine().dao();
	public List<TaskLine> getTls(String taskid){
	    return this.find("select  ptl.*   ,pt.cardCode from  pool_task pt,pool_task_line ptl " +
                " where pool_task_id=pt.id and  pt.task_type='1' and ptl.batch_num is null and ptl.pool_task_id=? order by pt.task_gen_datetime asc",taskid);
    }

    public List<TaskLine> getB2cTls(String no, String product_Class, String agentType, String sapSupplierID) {
        return this.find("select ptl.*,pt.cardCode from pool_task pt,pool_task_line ptl " +
        "where  ptl.pool_task_id=pt.id and  pt.task_type='0' and ptl.product_no =? and ptl.product_Class=? and pt.agentType=? and pt.sapSupplierID=? and ptl.batch_num is null and pt.erp_no is null and date(pt.task_gen_datetime)<= DATE_SUB(CURDATE(),INTERVAL 1 DAY) order by pt.task_gen_datetime asc "
                 ,no,product_Class,agentType,sapSupplierID);
    }
}
