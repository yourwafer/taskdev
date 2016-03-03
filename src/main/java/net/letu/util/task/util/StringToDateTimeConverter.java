package net.letu.util.task.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;

public class StringToDateTimeConverter implements Converter<String, Date> {

	private static final Logger logger = LoggerFactory.getLogger(StringToDateTimeConverter.class);

	private static final Map<Integer, String> FORMATTERS_MAP = new HashMap<Integer, String>();

	static {
		FORMATTERS_MAP.put("yyyy-MM-dd".length(), "yyyy-MM-dd");
		FORMATTERS_MAP.put("yyyy-MM-dd HH:mm:ss".length(), "yyyy-MM-dd HH:mm:ss");
		FORMATTERS_MAP.put("yyyyMMddHHmmss".length(), "yyyyMMddHHmmss");
	}

	@Override
	public Date convert(String source) {
		String format = FORMATTERS_MAP.get(source.length());
		if (format == null) {
			logger.warn("字符串[{}]不符合日期格式要求", source);
			throw new IllegalArgumentException("字符串[" + source + "]不符合格式要求");
		}
		SimpleDateFormat parse = new SimpleDateFormat(format);
		try {
			return parse.parse(source);
		} catch (ParseException e) {
			logger.warn("字符串[{}]不符合格式要求[{}]", source);
		}
		throw new IllegalArgumentException("字符串[" + source + "]不符合格式要求");
	}
}
