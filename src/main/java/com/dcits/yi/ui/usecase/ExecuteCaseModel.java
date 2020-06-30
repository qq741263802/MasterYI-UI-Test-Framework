package com.dcits.yi.ui.usecase;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.dcits.yi.ui.GlobalTestConfig;
import com.dcits.yi.ui.element.BasePage;
import com.dcits.yi.ui.report.CaseReport;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;

/**
 * 测试用例执行对象
 * @author xuwangcheng
 * @version 20181012
 *
 */
public class ExecuteCaseModel {	
	
	private static final Log logger = LogFactory.get();
	
	private Set<String> browserType = new HashSet<String>();
	
	/**
	 * 用例执行方法，包含多个，按顺序执行
	 */
	private List<Method> methods = new ArrayList<Method>();
	/**
	 * 测试用例对象，与methods中的方法对应
	 */
	private List<Object> targets = new ArrayList<Object>();
	
	/**
	 * 用例名称
	 */
	private String name;
	/**
	 * 重试次数
	 */
	private int retryCount;
	/**
	 * 失败中断标记
	 */
	private boolean failInterrupt;
	/**
	 * 标签
	 */
	private String tag;
	
	private boolean successFlag = true;
	
	/**
	 * 执行该用例的自动化测试
	 * @param browserName
	 */
	public void execute(String browserName) {
		logger.info("开始执行测试用例 {}[tag={}][broswerType={}] ", name, tag, browserName);
		
		CaseReport r = new CaseReport();
		r.setBrowserType(browserName);
		r.setCaseName(name);
		r.setCaseMethodPath(getMethodPaths());
		GlobalTestConfig.getTestRunningObject().setCaseReport(r);
		int count = 0;
		TimeInterval interval = new TimeInterval();
		interval.start();
		
		try {
			//按顺序执行用例方法
			for (int i = 0;i < methods.size();i++) {
				Method m = methods.get(i);
				m.invoke(targets.get(i));						
			}				
			logger.info("测试用例 [{}][tag={}][broswerType={}] 执行成功!", name, tag, browserName);
		} catch (Exception e) {
			logger.error(e, "执行测试用例 [{}][tag={}][broswerType={}] 失败!", name, tag, browserName);			
			this.successFlag = false;
			GlobalTestConfig.getTestRunningObject().getStepReport().setStatus(false);			
			r.setStatus("fail");
			r.setMark(GlobalTestConfig.getTestRunningObject().getStepReport().getMark());						
			GlobalTestConfig.getTestRunningObject().getStepReport().setStepName();
			
			BasePage.screenshot();
		}
		
		r.setUseTime(String.valueOf(interval.intervalMs()));
		r.setRunCount(count);
		r.setFinishTime(DateUtil.now());
		
		GlobalTestConfig.getTestRunningObject().setStepReport(null);
		GlobalTestConfig.getTestRunningObject().setCaseReport(null);	
	}
	
	public String getMethodPaths () {
		List<String> s = new ArrayList<String>();
		for (Method m:methods) {
			s.add(m.getDeclaringClass().getName() + "." + m.getName());
		}
		return StrUtil.join("\n", s);
	}
	
	public void setBrowserType(Set<String> browserType) {
		this.browserType = browserType;
	}
	
	public Set<String> getBrowserType() {
		return browserType;
	}
	
	public boolean isSuccessFlag() {
		return successFlag;
	}
	
	public List<Method> getMethods() {
		return methods;
	}
	public void setMethods(List<Method> methods) {
		this.methods = methods;
	}
	public List<Object> getTargets() {
		return targets;
	}
	public void setTargets(List<Object> targets) {
		this.targets = targets;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}	
	public int getRetryCount() {
		return retryCount;
	}
	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}
	public boolean isFailInterrupt() {
		return failInterrupt;
	}
	public void setFailInterrupt(boolean failInterrupt) {
		this.failInterrupt = failInterrupt;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
}
