package net.letu.util.task.model;

import java.util.Collection;

/**
 * 请求添加协助关系
 * @author yourwafer@163.com
 * @date 2016年2月23日 下午5:07:27
 */
public class ReqAssist {
	/** 任务ID */
	private String taskId;

	/** 协助人员 */
	private Collection<String> members;

	/** 时间 */
	private long time;

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public Collection<String> getMembers() {
		return members;
	}

	public void setMembers(Collection<String> members) {
		this.members = members;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

}
