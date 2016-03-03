package net.letu.util.task.model;

import java.util.HashSet;
import java.util.Set;

/**
 * 成员返回值
 * @author yourwafer@163.com
 * @date 2016年2月23日 下午4:33:19
 */
public class ResMember {
	/** 唯一ID */
	private String id;

	/** 成员名 */
	private String name;

	/** 密码 */
	private String password;

	/** 部门 */
	private String department;

	/** 是否有效 */
	private boolean valid;

	/** 权限 */
	private Set<Auth> auths = new HashSet<>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public Set<Auth> getAuths() {
		return auths;
	}

	public void setAuths(Set<Auth> auths) {
		this.auths = auths;
	}

	public static ResMember of(String id, String name, String password, String department, boolean valid, Set<Auth> auths) {
		ResMember m = new ResMember();
		m.id = id;
		m.name = name;
		m.password = password;
		m.department = department;
		m.valid = valid;
		m.auths = auths;
		return m;
	}

}
