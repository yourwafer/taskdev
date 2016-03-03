package net.letu.util.task.entity;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import net.letu.util.task.model.Auth;

/**
 * 成员
 * @author yourwafer@163.com
 * @date 2016年2月18日 上午10:35:25
 */
public class Member {

	/** 唯一ID */
	private String id;

	/** 成员名 */
	private String name;

	/** 密码 */
	private String password;

	/** 部门ID */
	private String department;

	/** 是否有效 */
	private boolean valid;

	/** 权限 */
	private Set<Auth> auths = new CopyOnWriteArraySet<>();

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
		this.auths = new CopyOnWriteArraySet<>(auths);
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public static Member of(String id, String name, String password, String department, boolean valid, Set<Auth> auths) {
		Member m = new Member();
		m.id = id;
		m.name = name;
		m.password = password;
		m.department = department;
		m.valid = valid;
		m.auths = new CopyOnWriteArraySet<>(auths);
		return m;
	}

}
