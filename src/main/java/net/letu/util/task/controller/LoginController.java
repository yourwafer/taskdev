package net.letu.util.task.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

import net.letu.common.utils.codec.CryptUtils;
import net.letu.util.task.ServerConfig;
import net.letu.util.task.entity.Department;
import net.letu.util.task.entity.Member;
import net.letu.util.task.model.ResMember;
import net.letu.util.task.service.db.DepartmentDbSupport;
import net.letu.util.task.service.db.MemberDbSupport;

/**
 * 登录接口
 * @author yourwafer@163.com
 * @date 2016年2月19日 上午11:32:52
 */
@Controller
@RequestMapping("api/login")
public class LoginController {

	private final String SUC = "redirect:/catalog.html";
	private final String FAI = "redirect:/index.html?";

	private enum Message {
		USER,
		PASSWORD
	}

	@Autowired
	private MemberDbSupport memberSupport;
	@Autowired
	private DepartmentDbSupport departmentSupport;

	/**
	 * 登录
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST)
	public String login(WebRequest request, HttpSession session) {
		String name = request.getParameter("username");
		Member member = memberSupport.load(name);
		if (member == null) {
			return FAI + Message.USER.name();
		}
		String passowrd = request.getParameter("password");
		if (passowrd == null) {
			return FAI + Message.PASSWORD.name();
		}
		if (CryptUtils.md5(passowrd).equals(member.getPassword())) {
			session.setAttribute(ServerConfig.IDENTITY, name);
			return SUC;
		}
		return FAI + Message.PASSWORD.name();
	}

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public ResMember getLoginInfo(HttpSession session) {
		Object name = session.getAttribute(ServerConfig.IDENTITY);
		if (name == null) {
			return new ResMember();
		}
		Member i = memberSupport.load(name.toString());
		Department de = departmentSupport.load(i.getDepartment());
		if (de == null) {
			ResMember member = ResMember.of(i.getId(), i.getName(), i.getPassword(), i.getDepartment(), i.isValid(), i.getAuths());
			return member;
		}
		ResMember member = ResMember.of(i.getId(), i.getName(), i.getPassword(), de.getName(), i.isValid(), i.getAuths());
		return member;
	}

	@RequestMapping("out")
	public String logout(HttpSession session) {
		session.removeAttribute(ServerConfig.IDENTITY);
		return FAI;
	}

}
