package net.letu.util.task.controller;

import java.util.Collection;

import javax.servlet.http.HttpSession;

import org.apache.poi.openxml4j.exceptions.InvalidOperationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import net.letu.util.task.ServerConfig;
import net.letu.util.task.entity.Department;
import net.letu.util.task.entity.Member;
import net.letu.util.task.model.Auth;
import net.letu.util.task.service.db.DepartmentDbSupport;
import net.letu.util.task.service.db.MemberDbSupport;
import net.letu.util.task.util.AuthCheck;

/**
 * 部门服务接口
 * @author yourwafer@163.com
 * @date 2016年2月18日 下午3:22:06
 */
@RestController
@RequestMapping("api/department")
public class DepartmentController {

	@Autowired
	private DepartmentDbSupport departmentSupport;
	@Autowired
	private MemberDbSupport memberSupport;

	/** 返回所有部门 */
	@RequestMapping(method = RequestMethod.GET)
	public Collection<Department> getDepartments() {
		return departmentSupport.getData();
	}

	/** 添加部门 */
	@RequestMapping(method = RequestMethod.POST)
	public Collection<Department> addDepartment(@RequestBody String name, HttpSession session) {
		Object identity = session.getAttribute(ServerConfig.IDENTITY);
		if (identity == null) {
			throw new InvalidOperationException("没有登录");
		}
		Member i = memberSupport.load(identity.toString());
		// 权限检查
		AuthCheck.authCheck(i.getAuths(), Auth.ADD_DEPARTMENT);

		departmentSupport.add(name);
		Collection<Department> datas = departmentSupport.getData();
		return datas;
	}
}
