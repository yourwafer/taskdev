package net.letu.util.task.entity;

import java.util.Date;
import java.util.UUID;

/**
 * 任务
 * @author yourwafer@163.com
 * @date 2016年2月18日 上午11:25:00
 */
public class Task {

	/** 无意义ID */
	private String id;

	/** 成员ID */
	private String member;

	/** 任务说明 */
	private String job;

	/** 任务进度 */
	private int progress;

	/** 开始时间 */
	private Date start;

	/** 结束时间 */
	private Date end;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMember() {
		return member;
	}

	public void setMember(String member) {
		this.member = member;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public static Task of(String member, String job, int progress, Date start, Date end) {
		Task task = new Task();
		task.id = UUID.randomUUID().toString();
		task.member = member;
		task.job = job;
		task.progress = progress;
		task.start = start;
		task.end = end;
		return task;
	}

}
