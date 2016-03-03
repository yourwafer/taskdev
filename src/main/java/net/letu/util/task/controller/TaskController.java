package net.letu.util.task.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.apache.poi.openxml4j.exceptions.InvalidOperationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;

import net.letu.common.utils.json.JsonUtils;
import net.letu.common.utils.time.DateUtils;
import net.letu.util.task.ServerConfig;
import net.letu.util.task.entity.Member;
import net.letu.util.task.entity.Task;
import net.letu.util.task.model.Auth;
import net.letu.util.task.model.ReTask;
import net.letu.util.task.service.db.MemberDbSupport;
import net.letu.util.task.service.db.TaskDbSupport;
import net.letu.util.task.util.AuthCheck;

/**
 * 开发任务控制器
 * @author yourwafer@163.com
 * @date 2016年2月18日 上午10:30:16
 */
@RestController
@RequestMapping("api/task")
public class TaskController {

	private final Logger logger = LoggerFactory.getLogger(TaskController.class);

	@Autowired
	private TaskDbSupport taskSupport;
	@Autowired
	private MemberDbSupport memberSupport;

	/***
	 * 获取所有任务
	 * @param session
	 * @return
	 */
	@RequestMapping
	public Collection<ReTask> list(@RequestParam String fromS, @RequestParam String toS, HttpSession session) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		final Date from;
		final Date to;
		try {
			from = format.parse(fromS);
			to = format.parse(toS);
		} catch (ParseException e) {
			logger.error("日期异常", e);
			return Collections.emptyList();
		}
		return taskSupport.getData().stream().filter(i -> {
			if (i.getStart().before(from) || i.getEnd().after(to)) {
				return false;
			}
			return true;
		}).map(i -> {
			Member cur = memberSupport.loadById(i.getMember());
			ReTask reTask = ReTask.of(i.getId(), cur.getName(), i.getJob(), i.getProgress(), i.getStart().getTime(), i.getEnd().getTime());
			return reTask;
		}).collect(Collectors.toList());
	}

	/**
	 * 获取登录用户对应部门的任务
	 * @param from
	 * @param to
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "department", method = RequestMethod.GET)
	public Collection<ReTask> listDepart(@RequestParam String fromS, @RequestParam String toS, HttpSession session) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		final Date from;
		final Date to;
		try {
			from = format.parse(fromS);
			to = format.parse(toS);
		} catch (ParseException e) {
			logger.error("日期异常", e);
			return Collections.emptyList();
		}
		Object attribute = session.getAttribute(ServerConfig.IDENTITY);
		if (attribute == null) {
			return Collections.emptyList();
		}
		Member login = memberSupport.load((String) attribute);
		return taskSupport.getData().stream().filter(i -> {
			if (i.getStart().before(from) || i.getEnd().after(to)) {
				return false;
			}
			if (login.getDepartment() == null) {
				return true;
			}
			String member = i.getMember();
			Member cur = memberSupport.loadById(member);
			if (cur.getDepartment() != null && cur.getDepartment().equals(login.getDepartment())) {
				return true;
			}
			return false;
		}).map(i -> {
			Member cur = memberSupport.loadById(i.getMember());
			ReTask reTask = ReTask.of(i.getId(), cur.getName(), i.getJob(), i.getProgress(), i.getStart().getTime(), i.getEnd().getTime());
			return reTask;
		}).collect(Collectors.toList());
	}

	/**
	 * 添加任务
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "department", method = RequestMethod.POST)
	public Map<String, String> addTasks(@RequestBody String body, HttpSession session) {
		Object identity = session.getAttribute(ServerConfig.IDENTITY);
		if (identity == null) {
			throw new InvalidOperationException("没有登录");
		}
		Member i = memberSupport.load(identity.toString());
		// 权限检查
		AuthCheck.authCheck(i.getAuths(), Auth.ADD_TASK);

		Collection<ReTask> tasks = JsonUtils.string2GenericObject(body, new TypeReference<Collection<ReTask>>() {
		});
		Map<String, String> idMap = new HashMap<>(tasks.size());
		for (ReTask reTask : tasks) {
			Member member = memberSupport.load(reTask.getName());
			Task task = Task.of(member.getId(), reTask.getJob(), reTask.getProgress(), DateUtils.getFirstTime(new Date(reTask.getStart())),
					DateUtils.getFirstTime(new Date(reTask.getEnd())));
			task = taskSupport.addTask(task, reTask.getOverride());
			idMap.put(reTask.getId(), task.getId());
		}
		return idMap;
	}

	/**
	 * 删除任务
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "department/delete", method = RequestMethod.POST)
	public Map<String, String> deleteTasks(@RequestBody String body, HttpSession session) {
		Object identity = session.getAttribute(ServerConfig.IDENTITY);
		if (identity == null) {
			throw new InvalidOperationException("没有登录");
		}
		Member i = memberSupport.load(identity.toString());
		// 权限检查
		AuthCheck.authCheck(i.getAuths(), Auth.DEL_TASK);

		Collection<ReTask> tasks = JsonUtils.string2GenericObject(body, new TypeReference<Collection<ReTask>>() {
		});
		for (ReTask reTask : tasks) {
			taskSupport.deleteTask(DateUtils.getFirstTime(new Date(reTask.getStart())),
					DateUtils.getFirstTime(new Date(reTask.getEnd())), reTask.getOverride());
		}
		return Collections.emptyMap();
	}

	/**
	 * 更新任务进度
	 * @param id
	 * @param progress
	 * @return
	 */
	@RequestMapping(value = "progress", method = RequestMethod.POST)
	public Collection<String> updateProgress(@RequestBody String body, HttpSession session) {
		Object identity = session.getAttribute(ServerConfig.IDENTITY);
		if (identity == null) {
			throw new InvalidOperationException("没有登录");
		}
		Member i = memberSupport.load(identity.toString());
		// 权限检查
		AuthCheck.authCheck(i.getAuths(), Auth.UPDATE_PROGRESS);

		Map<String, String> map = JsonUtils.string2Map(body, String.class, String.class);
		String task = map.get("task");
		Integer progress = Integer.valueOf(map.get("progress"));
		taskSupport.updateProgress(task, progress);
		return Collections.emptyList();
	}

	/**
	 * 更新任务
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "update", method = RequestMethod.POST)
	@ResponseBody
	public ReTask updateTask(@RequestBody String body) {
		ReTask task = JsonUtils.string2Object(body, ReTask.class);
		Task i = taskSupport.updateTask(task.getId(), task.getJob(), new Date(task.getStart()), new Date(task.getEnd()));
		Member cur = memberSupport.loadById(i.getMember());
		ReTask reTask = ReTask.of(i.getId(), cur.getName(), i.getJob(), i.getProgress(), i.getStart().getTime(), i.getEnd().getTime());
		return reTask;
	}

	/**
	 * 删除任务
	 * @param id
	 */
	@RequestMapping(value = "delete/{id}", method = RequestMethod.GET)
	@ResponseBody
	public ReTask deleteTask(@PathVariable String id) {
		Task i = taskSupport.deleteTask(id);
		if (i == null) {
			return null;
		}
		Member cur = memberSupport.loadById(i.getMember());
		ReTask reTask = ReTask.of(i.getId(), cur.getName(), i.getJob(), i.getProgress(), i.getStart().getTime(), i.getEnd().getTime());
		return reTask;
	}
}
