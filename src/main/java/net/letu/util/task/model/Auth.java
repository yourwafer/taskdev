package net.letu.util.task.model;

/**
 * 权限控制
 * @author yourwafer@163.com
 * @date 2016年2月18日 上午11:11:45
 */
public enum Auth {

	ADD_DEPARTMENT("添加部门"),

	ADD_MEMBER("添加成员"),

	REMOVE_USER("删除用户"),

	UPDATE_PROGRESS("更新任务进度"),

	ADD_ASSIST("添加协助关系"),

	ADD_TASK("添加任务"),

	DEL_TASK("删除任务");

	public String describe;

	private Auth(String describe) {
		this.describe = describe;
	}
}
