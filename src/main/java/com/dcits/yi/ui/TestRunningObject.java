package com.dcits.yi.ui;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.WebDriver;

import com.dcits.yi.constant.TestConst;
import com.dcits.yi.ui.data.BaseDataModel;
import com.dcits.yi.ui.driver.SeleniumDriver;
import com.dcits.yi.ui.element.Locator;
import com.dcits.yi.ui.report.CaseReport;
import com.dcits.yi.ui.report.StepReport;

import cn.hutool.core.util.ReUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;

/**
 * 分布式测试时，属于每个线程的测试运行对象
 * @author xuwangcheng
 * @version 20181012
 *
 */
public class TestRunningObject {
	private static final Log logger = LogFactory.get();
	
	/**
	 * 当前线程中的执行webDriver
	 */
	private WebDriver driver;
	/**
	 * 当前线程中不同类型的webDriver, key为浏览器类型常量
	 */
	private Map<String, WebDriver> drivers = new HashMap<String, WebDriver>();
	/**
	 * 当前时间的测试用例报告对象
	 */
	private CaseReport caseReport;
	/**
	 * 当前时间的测试步骤报告对象
	 */
	private StepReport stepReport;
	/**
	 * 当前所在frame名称
	 */
	private String currentFrameName = TestConst.DEFAULT_FRAME_NAME;
	/**
	 * 当前窗口handle
	 */
	private String currentWindowHandle = null;
	
	/**
	 * 当前使用的测试数据
	 */
	private List<BaseDataModel> datas = new ArrayList<BaseDataModel>();
	
	public WebDriver getDriver() {
		return driver;
	}
	
	public void setDriver(String browserType) throws MalformedURLException {
		driver = drivers.get(browserType.toLowerCase());
		if (driver == null) {
			driver = SeleniumDriver.initWebDriver(browserType);
			drivers.put(browserType.toLowerCase(), driver);
		}
	}
	
	public void setCurrentFrameName(String currentFrameName) {
		this.currentFrameName = currentFrameName;
	}
	
	public String getCurrentFrameName() {
		return currentFrameName;
	}

	/**
	 * 在每步操作之后都会检查窗口是否变动
	 */
	public void checkWindow(String methodName) {
		if (!this.driver.getWindowHandle().equals(currentWindowHandle)) {
			currentWindowHandle = this.driver.getWindowHandle();
			currentFrameName = TestConst.DEFAULT_FRAME_NAME;
		} else if (ReUtil.isMatch("open|close|refresh|forward|back|to", methodName)) {
			currentFrameName = TestConst.DEFAULT_FRAME_NAME;
		}
	}
	
	/**
	 * 切换到指定的frame层
	 * @param locator frame元素定位器
	 * @throws Exception
	 */
	public void switchFrame(Locator locator) throws Exception {
		if (locator == null) {
			logger.info("Switch To DefaultContent...");
			driver.switchTo().defaultContent();
			currentFrameName = TestConst.DEFAULT_FRAME_NAME;
			return;
		}
		logger.info("Switch Frame to {}:{} => {}[{}]", locator.getName(), locator.getLocationType(), locator.getLocationValue(), locator.getLocationSeq());
		try {
			driver.switchTo().frame(locator.getElement(driver));
			currentFrameName = locator.getName();
		} catch (Exception e) {
			logger.info("Switch To Frame {}:{} => {}[{}] Fail!", locator.getName(), locator.getLocationType(), locator.getLocationValue(), locator.getLocationSeq());
			throw e;
		}
	}
	
	public CaseReport getCaseReport() {
		return caseReport;
	}
	public void setCaseReport(CaseReport caseReport) {
		if (this.caseReport != null) {
			GlobalTestConfig.report.getCaseReports().add(this.caseReport);
		}
		this.caseReport = caseReport;
	}
	public StepReport getStepReport() {
		return stepReport;
	}
	public void setStepReport(StepReport stepReport) {
		if (this.caseReport != null && this.stepReport != null) {
			this.caseReport.getStepReports().add(this.stepReport);
		}
		this.stepReport = stepReport;
	}
	
	public Map<String, WebDriver> getDrivers() {
		return drivers;
	}
	
	public void setDrivers(Map<String, WebDriver> drivers) {
		this.drivers = drivers;
	}
	
	public void setDatas(List<BaseDataModel> datas) {
		this.datas = datas;
	}
	
	public List<BaseDataModel> getDatas() {
		return datas;
	}
	
}
