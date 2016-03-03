package net.letu.util.task.controller;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.apache.poi.openxml4j.exceptions.InvalidOperationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.letu.common.utils.json.JsonUtils;
import net.letu.common.utils.time.DateUtils;
import net.letu.util.task.ServerConfig;
import net.letu.util.task.entity.Member;
import net.letu.util.task.entity.Task;
import net.letu.util.task.model.Auth;
import net.letu.util.task.model.ReqAssist;
import net.letu.util.task.model.ResAssist;
import net.letu.util.task.service.db.AssistDbSupport;
import net.letu.util.task.service.db.MemberDbSupport;
import net.letu.util.task.service.db.TaskDbSupport;
import net.letu.util.task.util.AuthCheck;

/**
 * 协助关系
 * @author yourwafer@163.com
 * @date 2016年2月23日 下午5:09:28
 */
@RestController
@RequestMapping("api/assist")
public class AssistController {

	@Autowired
	private AssistDbSupport assistSupport;
	@Autowired
	private MemberDbSupport memberSupport;
	@Autowired
	private TaskDbSupport taskSupport;

	@RequestMapping(method = RequestMethod.GET)
	public Collection<ResAssist> getAssist(@RequestParam Date from, @RequestParam Date to) {
		return assistSupport.getData().stream().filter(i -> {
			if (i.getStart().before(from) || i.getStart().after(to)) {
				return false;
			}
			return true;
		}).map(i -> {
			Collection<String> memberIds = i.getMember();
			Collection<String> members = new HashSet<>(memberIds.size());
			memberIds.forEach(id -> {
				Member cur = memberSupport.loadById(id);
				members.add(cur.getName());
			});
			Task task = taskSupport.load(i.getTask());
			Member member = memberSupport.loadById(task.getMember());
			ResAssist assist = ResAssist.of(i.getId(), member.getName(), i.getTask(), members, i.getStart().getTime());
			return assist;
		}).collect(Collectors.toList());
	}

	@RequestMapping(method = RequestMethod.POST)
	public Collection<String> addAssist(@RequestBody String body, HttpSession session) {
		Object name = session.getAttribute(ServerConfig.IDENTITY);
		if (name == null) {
			throw new InvalidOperationException("没有登录");
		}
		Member i = memberSupport.load(name.toString());
		// 权限检查
		AuthCheck.authCheck(i.getAuths(), Auth.ADD_ASSIST);

		ReqAssist assist = JsonUtils.string2Object(body, ReqAssist.class);
		String task = assist.getTaskId();
		Collection<String> members = assist.getMembers();
		long time = assist.getTime();
		assistSupport.addAssist(task, members, DateUtils.getFirstTime(new Date(time)));
		return Collections.emptyList();
	}
}
