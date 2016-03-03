package net.letu.util.task.entity;

/**
 * 部门实体
 * @author yourwafer@163.com
 * @date 2016年2月18日 上午10:32:02
 */
public class Department {

	/** 唯一ID */
	private String id;

	/** 部门名称 */
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public static Department of(String id, String name) {
		Department d = new Department();
		d.id = id;
		d.name = name;
		return d;
	}

}
