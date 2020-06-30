package com.dcits.yi;

import org.springframework.boot.SpringApplication;

import com.dcits.yi.springboot.Application;
import com.dcits.yi.ui.EnvSettingInfo;
import com.dcits.yi.ui.GlobalTestConfig;

/**
 * 执行入口：以jar包方式运行测试
 * @author xuwangcheng
 * @version 20181012
 *
 */
public class StartTest {
	public static void main(String[] args) throws Exception {
		EnvSettingInfo.DEV_MODE = false;
		
		if (args.length == 0) {
			System.out.println("缺少参数, [-start-web]启动spring boot，[-start-cron]启动定时任务");
			System.exit(0);
		}
		if ("-start-web".equalsIgnoreCase(args[0])) {
			SpringApplication.run(Application.class);
		} else if ("-start-cron".equalsIgnoreCase(args[0])) {
			WebTest test = new WebTest(GlobalTestConfig.ENV_INFO.getCronSuite());
			test.startCron();
		} else {
			WebTest test = new WebTest(args[0]);		
			test.start();
		}	
	}
}
