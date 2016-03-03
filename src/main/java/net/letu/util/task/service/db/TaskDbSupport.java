package net.letu.util.task.service.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import net.letu.common.utils.collection.CopyOnWriteHashMap;
import net.letu.util.task.ServerConfig;
import net.letu.util.task.entity.Task;

/**
 * 任务操作协助类
 * @author yourwafer@163.com
 * @date 2016年2月18日 上午11:43:30
 */
@Component
public class TaskDbSupport extends AbstractDbSupport<Task> {
	@Autowired
	private ServerConfig config;

	private CopyOnWriteHashMap<String, Task> tasks = new CopyOnWriteHashMap<>();

	@PostConstruct
	void init() {
		Collection<Task> datas = read();
		if (!datas.isEmpty()) {
			initPreData(datas);
		}
	}

	public synchronized Task addTask(Task task, Collection<String> overrideIds) {
		// 覆盖了其他任务的开始及完成时间
		if (!CollectionUtils.isEmpty(overrideIds)) {
			List<Task> overrides = new ArrayList<>(overrideIds.size());
			overrideIds.forEach(i -> {
				Task t = tasks.get(i);
				if (t == null) {
					return;
				}
				overrides.add(t);
			});
			overrides.sort((i, j) -> {
				if (i.getStart().before(j.getStart())) {
					return -1;
				}
				return 1;
			});
			long start = task.getStart().getTime();
			long end = task.getEnd().getTime();
			overrides.forEach(t -> {
				long tmpStart = t.getStart().getTime();
				long tmpEnd = t.getEnd().getTime();
				// 无交接
				if (end < tmpStart || start > tmpEnd) {
					return;
				}
				// 前半部分
				if (start <= tmpStart) {
					// 完全包含，则此任务将被移除
					if (end >= tmpEnd) {
						tasks.remove(t.getId());
					} else {
						t.setStart(DateUtils.addDays(task.getEnd(), 1));
						if (t.getStart().after(t.getEnd())) {
							tasks.remove(t.getId());
						}
					}
				} else {
					if (end >= tmpEnd) {
						t.setEnd(DateUtils.addDays(task.getStart(), -1));
						if (t.getStart().after(t.getEnd())) {
							tasks.remove(t.getId());
						}
					} else {
						Task newTask = Task.of(t.getMember(), t.getJob(), t.getProgress(), t.getStart(), t.getEnd());
						t.setEnd(DateUtils.addDays(task.getStart(), -1));
						if (t.getStart().after(t.getEnd())) {
							tasks.remove(t.getId());
						}
						newTask.setStart(DateUtils.addDays(task.getEnd(), 1));
						if (!newTask.getEnd().before(newTask.getStart())) {
							addTask(newTask, null);
						}
					}
				}
			});
		}
		tasks.put(task.getId(), task);
		update();
		return task;
	}

	public Task load(String id) {
		return tasks.get(id);
	}

	/**
	 * 删除任务
	 * @param start
	 * @param end
	 * @param ids
	 */
	public void deleteTask(Date from, Date to, Collection<String> ids) {
		if (!CollectionUtils.isEmpty(ids)) {
			ids = new HashSet<>(ids);
			List<Task> overrides = new ArrayList<>(ids.size());
			ids.forEach(i -> {
				Task t = tasks.get(i);
				if (t == null) {
					return;
				}
				overrides.add(t);
			});
			overrides.sort((i, j) -> {
				if (i.getStart().before(j.getStart())) {
					return -1;
				}
				return 1;
			});
			long start = from.getTime();
			long end = to.getTime();
			overrides.forEach(t -> {
				long tmpStart = t.getStart().getTime();
				long tmpEnd = t.getEnd().getTime();
				// 无交接
				if (end < tmpStart || start > tmpEnd) {
					return;
				}
				// 前半部分
				if (start <= tmpStart) {
					// 完全包含，则此任务将被移除
					if (end >= tmpEnd) {
						tasks.remove(t.getId());
					} else {
						t.setStart(DateUtils.addDays(to, 1));
						if (t.getStart().after(t.getEnd())) {
							tasks.remove(t.getId());
						}
					}
				} else {
					if (end >= tmpEnd) {
						t.setEnd(DateUtils.addDays(from, -1));
						if (t.getStart().after(t.getEnd())) {
							tasks.remove(t.getId());
						}
					} else {
						Task newTask = Task.of(t.getMember(), t.getJob(), t.getProgress(), t.getStart(), t.getEnd());
						t.setEnd(DateUtils.addDays(from, -1));
						if (t.getStart().after(t.getEnd())) {
							tasks.remove(t.getId());
						}
						newTask.setStart(DateUtils.addDays(to, 1));
						if (!newTask.getEnd().before(newTask.getStart())) {
							addTask(newTask, null);
						}
					}
				}
			});
			update();
		}
	}

	public Task updateTask(String id, String content, Date start, Date end) {
		Task task = tasks.get(id);
		if (task == null) {
			return null;
		}
		if (StringUtils.isNotBlank(content)) {
			task.setJob(content);
		}
		task.setStart(start);
		task.setEnd(end);
		update();
		return task;
	}

	public Task deleteTask(String id) {
		Task task = tasks.remove(id);
		if (task != null) {
			update();
		}
		return task;
	}

	/**
	 * 更新任务进度
	 */
	public void updateProgress(String id, int progress) {
		Task task = tasks.get(id);
		if (task == null) {
			throw new IllegalArgumentException("任务不存在");
		}
		if (progress < 0) {
			progress = 0;
		}
		if (progress > 100) {
			progress = 100;
		}
		if (task.getProgress() != progress) {
			task.setProgress(progress);
			update();
		}
	}

	void initPreData(Collection<Task> data) {
		if (CollectionUtils.isEmpty(data)) {
			return;
		}
		data.forEach(i -> {
			tasks.put(i.getId(), i);
		});
	}

	@Override
	Class<Task> getClz() {
		return Task.class;
	}

	@Override
	public Collection<Task> getData() {
		return tasks.values();
	}

	@Override
	String getPath() {
		return config.getTask();
	}

}
