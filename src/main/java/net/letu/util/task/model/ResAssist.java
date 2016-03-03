package net.letu.util.task.model;

import java.util.Collection;

/**
 * 协助关系
 * @author yourwafer@163.com
 * @date 2016年2月23日 下午5:23:26
 */
public class ResAssist {
	/** 唯一ID */
	private String id;

	/** 任务ID */
	private String task;

	/** 用户 */
	private String owner;

	/** 成员ID */
	private Collection<String> member;

	/** 协助开始 */
	private long start;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTask() {
		return task;
	}

	public void setTask(String task) {
		this.task = task;
	}

	public Collection<String> getMember() {
		return member;
	}

	public void setMember(Collection<String> member) {
		this.member = member;
	}

	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public static ResAssist of(String id, String owner, String task, Collection<String> member, long start) {
		ResAssist a = new ResAssist();
		a.id = id;
		a.owner = owner;
		a.task = task;
		a.member = member;
		a.start = start;
		return a;
	}
}
