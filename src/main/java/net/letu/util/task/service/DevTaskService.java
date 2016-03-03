package net.letu.util.task.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.letu.util.task.entity.Department;
import net.letu.util.task.entity.Member;
import net.letu.util.task.service.db.DepartmentDbSupport;
import net.letu.util.task.service.db.MemberDbSupport;

/**
 * 提供服务接口
 * @author yourwafer@163.com
 * @date 2016年2月18日 上午11:40:11
 */
@Service
public class DevTaskService {

	@Autowired
	private MemberDbSupport memberSupport;
	@Autowired
	private DepartmentDbSupport departmentSupport;

	public Member getMember(String name) {
		return memberSupport.load(name);
	}

	/** 所有部门 */
	public Collection<Department> getAllDepartment() {
		return departmentSupport.getData();
	}

}
