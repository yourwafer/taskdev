package net.letu.util.task.service.db;

import java.util.Collection;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import net.letu.common.utils.collection.CopyOnWriteHashMap;
import net.letu.common.utils.json.JsonUtils;
import net.letu.util.task.ServerConfig;
import net.letu.util.task.entity.Department;

/**
 * 部门实体操作协助类
 * @author yourwafer@163.com
 * @date 2016年2月18日 上午11:42:22
 */
@Component
public class DepartmentDbSupport extends AbstractDbSupport<Department> {

	private final Logger logger = LoggerFactory.getLogger(DepartmentDbSupport.class);

	@Autowired
	private ServerConfig config;

	private CopyOnWriteHashMap<String, Department> departments = new CopyOnWriteHashMap<>();

	@PostConstruct
	void init() {
		Collection<Department> datas = read();
		if (!datas.isEmpty()) {
			initPreData(datas);
		}
	}

	public Department load(String id) {
		return departments.get(id);
	}

	public Department loadByName(String name) {
		for (Department de : departments.values()) {
			if (de.getName().equals(name)) {
				return de;
			}
		}
		return null;
	}

	void initPreData(Collection<Department> data) {
		if (CollectionUtils.isEmpty(data)) {
			return;
		}
		data.forEach(i -> {
			departments.put(i.getId(), i);
		});
	}

	public Department add(String name) {
		Department department = Department.of(UUID.randomUUID().toString(), name);
		departments.put(department.getId(), department);
		if (logger.isDebugEnabled()) {
			logger.debug("添加部门[{}]", JsonUtils.object2String(department));
		}
		update();
		return department;
	}

	@Override
	Class<Department> getClz() {
		return Department.class;
	}

	@Override
	public Collection<Department> getData() {
		return departments.values();
	}

	@Override
	String getPath() {
		return config.getDepartment();
	}

}
