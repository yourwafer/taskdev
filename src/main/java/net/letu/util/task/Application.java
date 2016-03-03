package net.letu.util.task;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import net.letu.util.task.util.StringToDateTimeConverter;

@SpringBootApplication
@ComponentScan({ "net.letu.util.task", "net.letu.common" })
public class Application extends WebMvcConfigurerAdapter {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void addFormatters(FormatterRegistry registry) {
		registry.addConverter(new StringToDateTimeConverter());
		super.addFormatters(registry);
	}

}
