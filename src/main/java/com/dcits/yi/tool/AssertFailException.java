package com.dcits.yi.tool;

import com.dcits.yi.ui.GlobalTestConfig;
import com.dcits.yi.ui.report.StepReport;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;

/**
 * 
* @version 1.0.0
* @Description 
* @author xuwangcheng
* @date 2019年1月11日下午3:32:17
 */
public class AssertFailException extends RuntimeException {
	private static final Log logger = LogFactory.get();

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	private AssertFailException(Throwable e) {
		super(ExceptionUtil.getMessage(e), e);
	}
	
	public AssertFailException(String message) {		
		super(message);
		mark(StrUtil.format(message));
	}
	
	public AssertFailException(String messageTemplate, Object... params) {
		super(StrUtil.format(messageTemplate, params));
		mark(StrUtil.format(messageTemplate, params));
	}
	
	public AssertFailException(String message, Throwable throwable) {
		super(message, throwable);
		mark(message);
	}
	
	public AssertFailException(Throwable throwable, String messageTemplate, Object... params) {		
		super(StrUtil.format(messageTemplate, params), throwable);
		mark(StrUtil.format(messageTemplate, params));
	}
	
	private void mark(String msg) {
		StepReport report = new StepReport();
		report.setActionName("断言");
		report.setMark(msg);
		report.setStatus(false);
		report.setStepName("断言失败！");
		GlobalTestConfig.getTestRunningObject().setStepReport(report);	
		logger.warn(msg);
	}
	
}
