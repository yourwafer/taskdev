package net.letu.util.task;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/***
 * server.properties配置文件
 * @author yourwafer@163.com
 * @date 2016年2月18日 上午11:47:27
 */
@Component
public class ServerConfig {

	public static final String IDENTITY = "letu:identity";

	/** 部门存储路径 */
	@Value("${department}")
	private String department;

	/** 成员存储路径 */
	@Value("${member}")
	private String member;

	/** 任务存储路径 */
	@Value("${task}")
	private String task;

	/** 协助关系存储路径 */
	@Value("${assist}")
	private String assist;

	/** 管理员密码 */
	@Value("${administrator}")
	private String administrator;

	public String getDepartment() {
		return department;
	}

	public String getMember() {
		return member;
	}

	public String getTask() {
		return task;
	}

	public String getAssist() {
		return assist;
	}

	public String getAdministrator() {
		return administrator;
	}

}
