package net.letu.util.task.model;

import java.util.Collection;

/**
 * 任务使用vo
 * @author yourwafer@163.com
 * @date 2016年2月20日 下午12:48:33
 */
public class ReTask {

	/** 任务ID */
	private String id;
	/** 成员名称 */
	private String name;

	/** 任务说明 */
	private String job;

	/** 任务进度 */
	private int progress;

	/** 开始时间 */
	private long start;

	/** 结束时间 */
	private long end;

	/** 覆盖的任务 */
	private Collection<String> override;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
	}

	public long getEnd() {
		return end;
	}

	public void setEnd(long end) {
		this.end = end;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Collection<String> getOverride() {
		return override;
	}

	public void setOverride(Collection<String> override) {
		this.override = override;
	}

	public static ReTask of(String id, String name, String job, int progress, long start, long end) {
		ReTask task = new ReTask();
		task.id = id;
		task.name = name;
		task.job = job;
		task.progress = progress;
		task.start = start;
		task.end = end;
		return task;
	}

}
