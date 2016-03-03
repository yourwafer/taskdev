package net.letu.util.task.entity;

import java.util.Collection;
import java.util.Date;
import java.util.UUID;

/**
 * 任务协助关系
 * @author yourwafer@163.com
 * @date 2016年2月18日 上午11:29:13
 */
public class Assist {

	/** 唯一ID */
	private String id;

	/** 任务ID */
	private String task;

	/** 成员ID */
	private Collection<String> member;

	/** 协助开始 */
	private Date start;

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

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public static Assist of(String task, Collection<String> member, Date start) {
		Assist a = new Assist();
		a.id = UUID.randomUUID().toString();
		a.task = task;
		a.member = member;
		a.start = start;
		return a;
	}

}
