package net.letu.util.task.util;

import java.util.Set;

import org.apache.poi.openxml4j.exceptions.InvalidOperationException;

import net.letu.util.task.model.Auth;

/**
 * 权限检查
 * @author yourwafer@163.com
 * @date 2016年2月24日 上午11:29:00
 */
public class AuthCheck {
	public static void authCheck(Set<Auth> auths, Auth auth) {
		for (Auth a : auths) {
			if (auth == a) {
				return;
			}
		}
		throw new InvalidOperationException("权限限制");
	}
}
