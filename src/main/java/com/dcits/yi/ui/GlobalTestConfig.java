package com.dcits.yi.ui;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import com.dcits.yi.tool.TestKit;
import com.dcits.yi.ui.data.db.TestDB;
import com.dcits.yi.ui.report.SuiteReport;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import cn.hutool.setting.dialect.Props;

/**
 * 全局配置
 * @author xuwangcheng
 * @version 20181012
 *
 */
public class GlobalTestConfig {
	
	public static final Log logger = LogFactory.get();
	
	public static EnvSettingInfo ENV_INFO;
	
	public static SuiteReport report;
	
	public static Map<String, TestDB> dbConnections = new HashMap<String, TestDB>();
	
	@SuppressWarnings("rawtypes")
	public static Map elements = new HashMap<>();	
	
	private static ThreadLocal<TestRunningObject> tro = new ThreadLocal<TestRunningObject>();
	
	/**
	 * 当前是否正在进行自动化测试
	 */
	public static AtomicBoolean testing = new AtomicBoolean(false);
	
	/**
	 * 定时任务id
	 */
	public static String cronTaskId;

	static {
		initFramework();
	}
	
	public static TestRunningObject getTestRunningObject() {
		TestRunningObject o = tro.get();
		if (o == null) {
			tro.set(new TestRunningObject());
		}
		return tro.get();
	}
	
	public static void removeTestRunningObject() {
		tro.remove();
	}
	
	/**
	 * 解析配置config文件
	 */
	private static void parseConfig() {	
		Props props = new Props(new File(TestKit.getProjectRootPath() + "/seleniumConfig.properties"));	
		
		ENV_INFO = new EnvSettingInfo(props);
		logger.info("详细配置信息：{}", ENV_INFO.toString());
		
		String dbs = props.getStr("dbname", "").trim();
		if (StrUtil.isNotEmpty(dbs)) {
			for (String dbName:dbs.split(",")) {
				try {
					dbConnections.put(dbName, TestDB.getInstance(props, dbName));
				} catch (Exception e) {
					logger.error(e, "获取标识名称为 [{}] 的数据连接失败!", dbName);
				}
			}
			
			logger.info("获取数据库信息成功:共  {} 条!", dbConnections.size());
		}			
	}	
	
	/**
	 * 解析定义元素的yaml文件
	 * @param filePath
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void parseElementYaml(String filePath) {
		for (File f:FileUtil.ls(filePath)) {
			if (FileUtil.isDirectory(f)) {
				parseElementYaml(f.getAbsolutePath());
			} else {
				try {
					Map m = TestKit.parseYaml(f.getAbsolutePath());
					if (m != null) {
						elements.putAll(m);
					}
				} catch (Exception e) {
				}				
			}
			
		}
	}
	
	/**
	 * 初始化测试框架
	 */
	public static void initFramework() {
		logger.info("解析测试配置文件seleniumConfig.properties");
		parseConfig();			
		logger.info("配置文件解析完成！");
		
		logger.info("解析element元素定义yaml文件...");
		parseElementYaml(ENV_INFO.getElementFolder());		
		logger.info("解析element元素定义yaml文件成功!");
	}
	
}
