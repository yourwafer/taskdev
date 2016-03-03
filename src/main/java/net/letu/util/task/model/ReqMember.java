package net.letu.util.task.model;

import java.util.HashSet;
import java.util.Set;

import net.letu.common.resource.JsonObject;

/**
 * 成员
 * @author yourwafer@163.com
 * @date 2016年2月18日 上午10:35:25
 */
public class ReqMember implements JsonObject {

	/** 成员名 */
	private String name;

	/** 密码 */
	private String password;

	/** 部门ID */
	private String department;

	/** 权限 */
	private Set<Auth> auths = new HashSet<>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public Set<Auth> getAuths() {
		return auths;
	}

	public void setAuths(Set<Auth> auths) {
		this.auths = auths;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
