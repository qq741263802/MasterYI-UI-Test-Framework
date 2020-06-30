package com.dcits.yi.ui.element;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;

import com.dcits.yi.ui.GlobalTestConfig;
import com.dcits.yi.ui.report.CaseReport;
import com.dcits.yi.ui.report.StepReport;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;

/**
 * 基础测试对象（页面或者页面元素都适用）
 * @author xuwangcheng
 * @version 20181012
 *
 */
public abstract class BaseObject {
	
	private static final Log logger = LogFactory.get();
	public static final Map<String, String> LOCATION_TYPES = new HashMap<String, String>();
	
	static {
		LOCATION_TYPES.put("id", "Id");
		LOCATION_TYPES.put("linktext", "LinkText");
		LOCATION_TYPES.put("name", "Name");
		LOCATION_TYPES.put("tagname", "TagName");
		LOCATION_TYPES.put("xpath", "XPath");
		LOCATION_TYPES.put("classname", "ClassName");
		LOCATION_TYPES.put("partiallinktext", "PartialLinkText");
		LOCATION_TYPES.put("cssselector", "CssSelector");
	}
	
	/**
	 * 获取当前的WebDriver对象
	 * @return
	 */
	public WebDriver getDriver() {
		return GlobalTestConfig.getTestRunningObject().getDriver();
	}
	
	/**
	 * 获取当前的步骤报告对象
	 * @return
	 */
	public StepReport getStepReport() {
		return GlobalTestConfig.getTestRunningObject().getStepReport();
	}
	
	protected void setStepReport() {
		GlobalTestConfig.getTestRunningObject().setStepReport(new StepReport());
	}
	/**
	 * 获取当前的测试用例报告
	 * @return
	 */
	public CaseReport getCaseReport() {
		return GlobalTestConfig.getTestRunningObject().getCaseReport();
	}
	
	/**
	 * 等待时间,秒
	 * @param seconds 秒，可小数
	 */
	public void sleep(double seconds) {
		try {
			Thread.sleep((int)(seconds * 1000));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			logger.warn(e, "InterruptedException！");
		}
	}
	
}
