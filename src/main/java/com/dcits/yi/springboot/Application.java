package com.dcits.yi.springboot;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import com.dcits.yi.springboot.config.SpringConfiguration;

/**
 * 
* @version 1.0.0
* @Description 
* @author xuwangcheng
* @date 2019年1月11日下午3:31:14
 */
@SpringBootApplication
@Import(SpringConfiguration.class)
public class Application {
	
}
