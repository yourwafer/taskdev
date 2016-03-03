package net.letu.util.task.service.db;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import net.letu.common.utils.codec.CryptUtils;
import net.letu.common.utils.json.JsonUtils;
import net.letu.util.task.ServerConfig;
import net.letu.util.task.entity.Department;
import net.letu.util.task.entity.Member;
import net.letu.util.task.model.Auth;

/**
 * 成员操作协助类
 * @author yourwafer@163.com
 * @date 2016年2月18日 上午11:42:59
 */
@Component
public class MemberDbSupport extends AbstractDbSupport<Member> {

	private Logger logger = LoggerFactory.getLogger(MemberDbSupport.class);

	private final String ADMIN = "admin";

	@Autowired
	private ServerConfig config;

	@Autowired
	private DepartmentDbSupport departmentSupport;

	/** 缓存所有 */
	private ConcurrentHashMap<String, Member> members = new ConcurrentHashMap<String, Member>();
	private ConcurrentHashMap<String, Member> membersId = new ConcurrentHashMap<String, Member>();

	@PostConstruct
	void init() {
		Collection<Member> datas = read();
		if (datas.isEmpty()) {
			initDefault();
		} else {
			initPreData(datas);
		}
	}

	void initDefault() {
		String password = config.getAdministrator();
		Set<Auth> auths = new HashSet<>(Arrays.asList(Auth.values()));

		addMember(ADMIN, password, null, auths);
	}

	public Member addMember(String name, String password, String department, Set<Auth> auths) {
		Department de = departmentSupport.loadByName(department);
		if (de != null) {
			department = de.getId();
		} else {
			department = null;
		}
		Member member = members.get(name);
		if (member != null) {
			if (StringUtils.isNotBlank(password)) {
				member.setPassword(CryptUtils.md5(password));
			}
			if (auths != null) {
				member.setAuths(auths);
			}
			if (StringUtils.isNotBlank(department)) {
				member.setDepartment(department);
			}

		} else {
			member = Member.of(UUID.randomUUID().toString(), name, CryptUtils.md5(password), department, true, auths);
			members.put(member.getName(), member);
			membersId.put(member.getId(), member);
			logger.info("添加帐号[{}]", JsonUtils.object2String(member));
		}
		update();
		return member;
	}

	public Member remove(String name) {
		Member mem = members.get(name);
		if (mem == null) {
			return null;
		}
		members.remove(name);
		update();
		return mem;
	}

	void initPreData(Collection<Member> data) {
		if (CollectionUtils.isEmpty(data)) {
			return;
		}
		data.forEach(i -> {
			members.put(i.getName(), i);
			membersId.put(i.getId(), i);
		});
		Member member = members.get(ADMIN);
		if (member == null) {
			return;
		}
		String newPassword = CryptUtils.md5(config.getAdministrator());
		if (!member.getPassword().equals(newPassword)) {
			member.setPassword(newPassword);
		}
	}

	public Member load(String name) {
		return members.get(name);
	}

	public Member loadById(String id) {
		return membersId.get(id);
	}

	@Override
	Class<Member> getClz() {
		return Member.class;
	}

	@Override
	public Collection<Member> getData() {
		return members.values();
	}

	@Override
	String getPath() {
		return config.getMember();
	}

}
