package com.dcits.yi.springboot.config;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

import com.dcits.yi.tool.TestKit;

/**
 * 
* @version 1.0.0
* @Description 
* @author xuwangcheng
* @date 2019年1月11日下午3:31:58
 */
@Configurable
@ComponentScan({ "com.dcits.yi.springboot"})
public class SpringConfiguration {
	
	private String contentType = "text/html; chartset=UTF-8";
	private boolean cache = false;
	private String templateLoaderPath = TestKit.getProjectRootPath() + "/template/";
	private String charset = "UTF-8";
	
	@Bean
	public ViewResolver viewResolver() {
		FreeMarkerViewResolver resolver = new FreeMarkerViewResolver();
		resolver.setCache(cache);
		resolver.setPrefix("");
		resolver.setContentType(contentType);
		
		return resolver;
	}
	
	@Bean
	public FreeMarkerConfigurer freemarkerconfig() {
		FreeMarkerConfigurer config = new FreeMarkerConfigurer();
		config.setTemplateLoaderPath("file:" + templateLoaderPath);
		config.setDefaultEncoding(charset);
		return config;
	}
}
