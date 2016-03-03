package net.letu.util.task.service.db;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import net.letu.common.utils.collection.CopyOnWriteHashMap;
import net.letu.common.utils.time.DateUtils;
import net.letu.util.task.ServerConfig;
import net.letu.util.task.entity.Assist;
import net.letu.util.task.entity.Member;
import net.letu.util.task.entity.Task;

/**
 * 协助关系实体操作类
 * @author yourwafer@163.com
 * @date 2016年2月18日 上午11:41:36
 */
@Component
public class AssistDbSupport extends AbstractDbSupport<Assist> {

	@Autowired
	private ServerConfig config;
	@Autowired
	private MemberDbSupport memberSupport;
	@Autowired
	private TaskDbSupport taskSupport;

	public CopyOnWriteHashMap<String, Assist> assists = new CopyOnWriteHashMap<>();
	public CopyOnWriteHashMap<String, CopyOnWriteHashMap<Long, CopyOnWriteArraySet<String>>> taskAssists = new CopyOnWriteHashMap<>();

	@PostConstruct
	void init() {
		Collection<Assist> datas = read();
		if (!datas.isEmpty()) {
			initPreData(datas);
		}
	}

	public Assist addAssist(String taskId, Collection<String> members, Date time) {
		time = DateUtils.getFirstTime(time);
		Task task = taskSupport.load(taskId);
		if (task == null) {
			throw new IllegalArgumentException();
		}
		if (time.before(task.getStart()) || time.after(task.getEnd())) {
			throw new IllegalArgumentException();
		}
		Set<String> assistIds = new HashSet<>(members.size());
		for (String member : members) {
			Member load = memberSupport.load(member);
			if (load == null) {
				throw new IllegalArgumentException();
			}
			assistIds.add(load.getId());
		}
		CopyOnWriteHashMap<Long, CopyOnWriteArraySet<String>> copyOnWriteHashMap = taskAssists.get(taskId);
		if (copyOnWriteHashMap == null) {
			copyOnWriteHashMap = new CopyOnWriteHashMap<>();
			taskAssists.put(taskId, copyOnWriteHashMap);
		}
		CopyOnWriteArraySet<String> assistMembers = copyOnWriteHashMap.get(time.getTime());
		if (assistMembers == null) {
			assistMembers = new CopyOnWriteArraySet<>();
			copyOnWriteHashMap.put(time.getTime(), assistMembers);
		}
		assistIds.removeAll(assistMembers);
		if (assistIds.size() <= 0) {
			throw new IllegalArgumentException("重复协助关系");
		}
		assistMembers.addAll(assistIds);
		Assist assist = Assist.of(taskId, assistIds, time);
		assists.put(assist.getId(), assist);
		update();
		return assist;
	}

	void initPreData(Collection<Assist> data) {
		if (CollectionUtils.isEmpty(data)) {
			return;
		}
		data.forEach(i -> {
			assists.put(i.getId(), i);
			CopyOnWriteHashMap<Long, CopyOnWriteArraySet<String>> copyOnWriteHashMap = taskAssists.get(i.getTask());
			if (copyOnWriteHashMap == null) {
				copyOnWriteHashMap = new CopyOnWriteHashMap<>();
				taskAssists.put(i.getTask(), copyOnWriteHashMap);

			}
			CopyOnWriteArraySet<String> assistMembers = copyOnWriteHashMap.get(i.getStart().getTime());
			if (assistMembers == null) {
				assistMembers = new CopyOnWriteArraySet<>();
				copyOnWriteHashMap.put(i.getStart().getTime(), assistMembers);
			}
			assistMembers.addAll(i.getMember());
		});
	}

	@Override
	Class<Assist> getClz() {
		return Assist.class;
	}

	@Override
	public Collection<Assist> getData() {
		for (Assist k : assists.values()) {
			Task task = taskSupport.load(k.getTask());
			if (task == null) {
				assists.remove(k.getId());
			}
		}
		return assists.values();
	}

	@Override
	String getPath() {
		return config.getAssist();
	}

}
