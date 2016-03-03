package net.letu.util.task.service.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextException;
import org.springframework.util.CollectionUtils;

import net.letu.common.scheduler.ScheduledTask;
import net.letu.common.scheduler.Scheduler;
import net.letu.common.utils.io.FileUtils;
import net.letu.common.utils.json.JsonUtils;

/**
 * 抽象实体管理
 * @author yourwafer@163.com
 * @date 2016年2月18日 下午2:21:42
 */
public abstract class AbstractDbSupport<T> {

	private final Logger logger = LoggerFactory.getLogger(AbstractDbSupport.class);

	@Autowired
	private Scheduler scheduler;

	/***
	 * 管理的class
	 * @return
	 */
	abstract Class<T> getClz();

	/**
	 * 实体化数据
	 * @return
	 */
	public abstract Collection<T> getData();

	/**
	 * 存储路径
	 * @return
	 */
	abstract String getPath();

	/***
	 * 读取实体文件数据
	 * @return
	 */
	protected Collection<T> read() {
		File file = new File(getPath());
		// 读取之前保存的数据
		if (file.exists()) {
			try {
				List<String> lines = FileUtils.readLines(new FileInputStream(file), "UTF-8");
				if (CollectionUtils.isEmpty(lines)) {
					return Collections.emptySet();
				}
				Set<T> datas = new HashSet<>(lines.size());
				lines.forEach(i -> {
					if (StringUtils.isEmpty(i)) {
						return;
					}
					T object = JsonUtils.string2Object(i, getClz());
					datas.add(object);
				});
				return datas;
			} catch (IOException e) {
				throw new ApplicationContextException("启动异常", e);
			}
		} else {
			return Collections.emptySet();
		}
	}

	/** 发生变更，触发持久化 */
	protected void update() {
		scheduler.scheduleWithDelay(new ScheduledTask() {

			@Override
			public void run() {
				Collection<T> values = getData();
				List<String> contents = new ArrayList<>(values.size());
				values.forEach(i -> {
					String con = JsonUtils.object2String(i);
					contents.add(con);
				});
				File file = new File(getPath());
				try {
					if (!file.exists()) {
						File parentFile = file.getParentFile();
						if (!parentFile.exists()) {
							parentFile.mkdirs();
						} else {
							file.createNewFile();
						}
					}
					FileUtils.writeLines(file, "UTF-8", contents);
					logger.info("保存成员持久化");
				} catch (IOException e) {
					logger.error("持久化失败", e);
				}
			}

			@Override
			public String getName() {
				return "保存数据" + getClz().getSimpleName();
			}
		}, 100);
	}
}
