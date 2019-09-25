package com.order.www.orderInterface.entity;

import com.order.www.orderInterface.entity.BaseTaskLine;

import java.util.List;

/**
 * Generated by JFinal.
 */
@SuppressWarnings("serial")
public class TaskLine extends BaseTaskLine<TaskLine> {
    public static final TaskLine dao = new TaskLine().dao();
	public List<TaskLine> getTls(String taskid){
	    return this.find("select * from pool_task_line where  batch_num is null and pool_task_id=?",taskid);
    }

    public List<TaskLine> getB2cTls(String id, String s) {
        return this.find("select * from pool_task_line where  batch_num is null and pool_task_id=? and product_Class=?",id,s);
    }
}
