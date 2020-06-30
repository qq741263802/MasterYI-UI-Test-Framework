package com.dcits.yi.ui.aop;

import static com.dcits.yi.ui.GlobalTestConfig.getTestRunningObject;

import java.lang.reflect.Method;

import com.dcits.yi.constant.TestConst;
import com.dcits.yi.ui.report.StepReport;

import cn.hutool.aop.aspects.SimpleAspect;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;

/**
 * 操作步骤记录的日志切面
 * @author xuwangcheng
 * @version 20181012
 *
 */
public class CreateStepReportAspect extends SimpleAspect {
	private static final Log logger = LogFactory.get();

	@Override
	public boolean before(Object target, Method method, Object[] args) {
		if (TestConst.ACTION_KEYWORD.get(method.getName()) == null) {
			return true;
		}
		
		getTestRunningObject().setStepReport(new StepReport());	
		getTestRunningObject().getStepReport().setActionName(TestConst.ACTION_KEYWORD.get(method.getName()));		
		getTestRunningObject().getStepReport().setParams(StrUtil.join(",", args));	
		getTestRunningObject().getStepReport().setTestTime(DateUtil.now());		
		return true;
	}

	@Override
	public boolean after(Object target, Method method, Object[] args) {
		if (TestConst.ACTION_KEYWORD.get(method.getName()) == null) {
			return true;
		}
		getTestRunningObject().checkWindow(method.getName());
		getTestRunningObject().getStepReport().setStepName();
		
		StringBuilder info = new StringBuilder("StepName:" + getTestRunningObject().getStepReport().getStepName());
		if (StrUtil.isNotEmpty(getTestRunningObject().getStepReport().getLocation())) {
			info.append(", Element:" + getTestRunningObject().getStepReport().getLocation());
		}
		if (StrUtil.isNotEmpty(getTestRunningObject().getStepReport().getParams())) {
			info.append(", Params:" + getTestRunningObject().getStepReport().getParams());
		}
		if (StrUtil.isNotEmpty(getTestRunningObject().getStepReport().getResult())) {
			info.append(", Result:" + getTestRunningObject().getStepReport().getResult());
		}
		logger.info(info.toString());
		
		return true;
	}
}
