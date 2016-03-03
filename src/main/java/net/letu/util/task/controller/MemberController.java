package net.letu.util.task.controller;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.apache.poi.openxml4j.exceptions.InvalidOperationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import net.letu.common.utils.json.JsonUtils;
import net.letu.util.task.ServerConfig;
import net.letu.util.task.entity.Department;
import net.letu.util.task.entity.Member;
import net.letu.util.task.model.Auth;
import net.letu.util.task.model.ReqMember;
import net.letu.util.task.model.ResMember;
import net.letu.util.task.service.db.DepartmentDbSupport;
import net.letu.util.task.service.db.MemberDbSupport;
import net.letu.util.task.util.AuthCheck;

/**
 * 成员服务接口
 * @author yourwafer@163.com
 * @date 2016年2月18日 下午3:22:06
 */
@RestController
@RequestMapping("api/member")
public class MemberController {

	@Autowired
	private MemberDbSupport memberSupport;
	@Autowired
	private DepartmentDbSupport departmentSupport;

	/** 返回所有成员 */
	@RequestMapping(method = RequestMethod.GET)
	public Collection<ResMember> getDepartments() {
		return memberSupport.getData().stream().map(i -> {
			Department de = departmentSupport.load(i.getDepartment());
			if (de == null) {
				ResMember member = ResMember.of(i.getId(), i.getName(), i.getPassword(), i.getDepartment(), i.isValid(), i.getAuths());
				return member;
			}
			ResMember member = ResMember.of(i.getId(), i.getName(), i.getPassword(), de.getName(), i.isValid(), i.getAuths());
			return member;
		}).collect(Collectors.toList());
	}

	/** 返回登录用户对应部门所有成员 */
	@RequestMapping(value = "department", method = RequestMethod.GET)
	public Collection<ResMember> getDepartments(HttpSession session) {
		Object name = session.getAttribute(ServerConfig.IDENTITY);
		if (name == null) {
			return Collections.emptySet();
		}
		Member member = memberSupport.load(name.toString());
		Collection<Member> members = memberSupport.getData();
		return members.stream().filter(i -> {
			if (i.getDepartment() == null) {
				return false;
			} else {
				if (member.getDepartment() != null) {
					return i.getDepartment().equals(member.getDepartment());
				} else {
					return true;
				}
			}
		}).map(i -> {
			Department de = departmentSupport.load(i.getDepartment());
			String depart = "";
			if (de != null) {
				depart = de.getName();
			}
			ResMember m = ResMember.of(i.getId(), i.getName(), i.getPassword(), depart, i.isValid(), i.getAuths());
			return m;
		}).collect(Collectors.toList());

	}

	/** 权限说明 */
	@RequestMapping("auths")
	public Map<String, String> getAuths() {
		Map<String, String> auths = new HashMap<>();

		for (Auth auth : Auth.values()) {
			auths.put(auth.name(), auth.describe);
		}
		return auths;
	}

	/** 添加成员 */
	@RequestMapping(method = RequestMethod.POST)
	public Collection<ResMember> addMember(@RequestBody String body, HttpSession session) {
		Object identity = session.getAttribute(ServerConfig.IDENTITY);
		if (identity == null) {
			throw new InvalidOperationException("没有登录");
		}
		Member me = memberSupport.load(identity.toString());
		// 权限检查
		AuthCheck.authCheck(me.getAuths(), Auth.ADD_MEMBER);
		ReqMember member = JsonUtils.string2Object(body, ReqMember.class);
		memberSupport.addMember(member.getName(), member.getPassword(), member.getDepartment(), member.getAuths());
		return memberSupport.getData().stream().map(i -> {
			Department de = departmentSupport.load(i.getDepartment());
			String department = "";
			if (de != null) {
				department = de.getName();
			}
			ResMember m = ResMember.of(i.getId(), i.getName(), i.getPassword(), department, i.isValid(), i.getAuths());
			return m;
		}).collect(Collectors.toList());
	}

	@RequestMapping(value = "del/{user}", method = RequestMethod.GET)
	@ResponseBody
	public Collection<ResMember> removeMember(@PathVariable String user, HttpSession session) {
		Object identity = session.getAttribute(ServerConfig.IDENTITY);
		if (identity == null) {
			throw new InvalidOperationException("没有登录");
		}
		Member me = memberSupport.load(identity.toString());
		// 权限检查
		AuthCheck.authCheck(me.getAuths(), Auth.REMOVE_USER);

		memberSupport.remove(user);

		return memberSupport.getData().stream().map(i -> {
			Department de = departmentSupport.load(i.getDepartment());
			String department = "";
			if (de != null) {
				department = de.getName();
			}
			ResMember m = ResMember.of(i.getId(), i.getName(), i.getPassword(), department, i.isValid(), i.getAuths());
			return m;
		}).collect(Collectors.toList());
	}

}
