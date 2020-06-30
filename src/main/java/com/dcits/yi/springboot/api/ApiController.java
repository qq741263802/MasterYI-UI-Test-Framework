package com.dcits.yi.springboot.api;

import java.io.File;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.dcits.yi.WebTest;
import com.dcits.yi.springboot.ReturnJSONObject;
import com.dcits.yi.tool.TestKit;
import com.dcits.yi.ui.GlobalTestConfig;
import com.dcits.yi.ui.report.manage.ZTestReportManager;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.cron.CronUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;

/**
 * 
* @version 1.0.0
* @Description 
* @author xuwangcheng
* @date 2019年1月11日下午3:31:45
 */
@RestController
public class ApiController {
	private static final Log logger = LogFactory.get();
	
	
	/**
	 * 获取当前测试配置信息
	 * @return
	 */
	@RequestMapping(value = "/api/getSetting", method = RequestMethod.GET)
	public ReturnJSONObject getSetting() {		
		return new ReturnJSONObject().setData(GlobalTestConfig.ENV_INFO);
	}
	
	/**
	 * 重新加载配置文件信息：包括seleniumConfig.properties elements.yaml文件
	 * @return
	 */
	@RequestMapping(value = "/api/reloadSetting", method = RequestMethod.GET)
	public ReturnJSONObject reloadSetting() {
		GlobalTestConfig.initFramework();
		return new ReturnJSONObject().setData(GlobalTestConfig.ENV_INFO).setMsg("重新加载配置信息成功!");
	}
	
	/**
	 * 启动一次自动化测试
	 * @param suite suite配置文件名，不含后缀
	 * @return
	 */
	@RequestMapping(value = "/api/startTest", method = RequestMethod.GET)
	public ReturnJSONObject startTest(HttpServletRequest request) {
		ReturnJSONObject json = new ReturnJSONObject();
		if (GlobalTestConfig.testing.get()) {
			return json.setCode(ReturnJSONObject.FAIL_CODE).setMsg("当前有正在执行的测试任务").setData(GlobalTestConfig.report);
		}		
		String suiteName = request.getParameter("suite");
		
		if (StrUtil.isEmpty(suiteName)) {
			return json.setCode(ReturnJSONObject.FAIL_CODE).setMsg("缺少参数suite:请指定一个suite配置文件名称!");
		}
		
		try {
			final WebTest test = new WebTest(suiteName);
			ThreadUtil.execute(new Runnable() {			
				@Override
				public void run() {
					test.start();				
				}
			});
		} catch (Exception e) {
			return json.setCode(ReturnJSONObject.SYSTEM_ERROR_CODE).setMsg(ExceptionUtil.getMessage(e));
		}
		
		return json.setMsg("开启测试成功, 你可以通过/api/getReportData接口查看测试状态信息");
	}
	
	/**
	 * 启动定时测试任务
	 * @param suite suite配置文件名，不含后缀，如果没有提供则使用seleniumConfig.properties中的cron.suitename配置
	 * @param cron 定时规则表达式，如果没有提供则使用seleniumConfig.properties中的cron.expression配置
	 * @return
	 */
	@RequestMapping(value = "/api/startCronTest", method = RequestMethod.GET)
	public ReturnJSONObject startCronTest(HttpServletRequest request) {
		ReturnJSONObject json = new ReturnJSONObject();
		if (CronUtil.getScheduler().isStarted()) {
			return json.setCode(ReturnJSONObject.FAIL_CODE).setMsg("当前已启动定时任务,请先调用/api/stopCronTest停止再启动新的定时任务");
		}
		if (StrUtil.isNotEmpty(GlobalTestConfig.cronTaskId)) {
			CronUtil.remove(GlobalTestConfig.cronTaskId);
			GlobalTestConfig.cronTaskId = null;
		}
		
		String suiteName = request.getParameter("suite");
		String cronExpression = request.getParameter("cron");
		
		if (StrUtil.isEmpty(suiteName)) {
			suiteName = GlobalTestConfig.ENV_INFO.getCronSuite();
		}
		if (StrUtil.isEmpty(cronExpression)) {
			cronExpression = GlobalTestConfig.ENV_INFO.getCronExpression();
		}

		try {
			WebTest test = new WebTest(suiteName);
			test.startCron(cronExpression);
		} catch (Exception e) {
			return json.setCode(ReturnJSONObject.SYSTEM_ERROR_CODE).setMsg(StrUtil.format("SuiteName=[{}],CronExpression=[{}], Exception:[{}]"
					, suiteName, cronExpression, ExceptionUtil.getMessage(e)));
		}
			
		return json.setMsg(StrUtil.format("定时测试任务启动成功，SuiteName=[{}],CronExpression=[{}]", suiteName, cronExpression));
	}
	
	/**
	 * 停止当前正在运行的定时任务
	 * @return
	 */
	@RequestMapping(value = "/api/stopCronTest", method = RequestMethod.GET)
	public ReturnJSONObject stopCronTest() {
		ReturnJSONObject json = new ReturnJSONObject();
		if (!CronUtil.getScheduler().isStarted()) {
			return json.setMsg("当前无运行的定时任务");
		} 
		CronUtil.stop();
		CronUtil.remove(GlobalTestConfig.cronTaskId);
		GlobalTestConfig.cronTaskId = null;
		return json.setMsg("已成功停止定时任务");
	}
	
	/**
	 * 获取当前测试报告数据
	 * @return
	 */
	@RequestMapping(value = "/api/getReportData", method = RequestMethod.GET)
	public ReturnJSONObject getReportData() {
		return new ReturnJSONObject().setData(GlobalTestConfig.report).setMsg(GlobalTestConfig.report != null && GlobalTestConfig.report.isFinished() ? "当前测试已完成" : "当前无测试数据或者测试未完成");
	}
	
	/**
	 * 获取当前所有已生成的测试报告文件
	 * @return
	 */
	@RequestMapping(value = "/api/listReport", method = RequestMethod.GET)
	public ReturnJSONObject listReport() {
		ReturnJSONObject json = new ReturnJSONObject();
		//列出report目录下的所有文件
		JSONArray reports = new JSONArray();
		for (File f:FileUtil.ls(GlobalTestConfig.ENV_INFO.getReportFolder())) {
			if (FileUtil.isFile(f)) {
				reports.add(f.getName());
			}
		}		
		return json.setData(reports).setMsg("当前共有测试报告文件" + reports.size() + "个，你可以通过[/api/getReport?filename=文件名称]接口来下载");
	}
	
	/**
	 * 获取当前全部的suite配置文件
	 * @return
	 */
	@RequestMapping(value = "/api/listSuite", method = RequestMethod.GET)
	public ReturnJSONObject listSuite() {
		ReturnJSONObject json = new ReturnJSONObject();
		//列出suite目录下的所有文件
		JSONArray suites = new JSONArray();
		for (File f:FileUtil.ls(GlobalTestConfig.ENV_INFO.getSuiteFolder())) {
			if (FileUtil.isFile(f)) {
				suites.add(f.getName().substring(0, f.getName().lastIndexOf(".")));
			}
		}		
		return json.setData(suites).setMsg("当前共有suite文件" + suites.size() + "个，你可以通过[/api/viewSuite?suite=文件名]接口来查看详细内容");
	}
	
	/**
	 * 查看指定suite配置文件详细信息
	 * @param suite suite配置文件名称，不带后缀
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/api/viewSuite", method = RequestMethod.GET)
	public ReturnJSONObject viewSuite(HttpServletRequest request) {
		ReturnJSONObject json = new ReturnJSONObject();
		String suiteName = request.getParameter("suite");
		if (StrUtil.isEmpty(suiteName)) {
			return json.setCode(ReturnJSONObject.FAIL_CODE).setMsg("缺少参数suite");
		}
		
		String suite = GlobalTestConfig.ENV_INFO.getSuiteFolder() + "/" + suiteName + ".yaml";
		if (!FileUtil.exist(suite)) {
			return json.setCode(ReturnJSONObject.FAIL_CODE).setMsg("suite配置文件" + suiteName + "不存在,请检查!");
		}
		
		try {
			Map m = TestKit.parseYaml(suite);
			json.setData(m);
		} catch (Exception e) {
			logger.error(e, "解析suite配置文件[{}.yaml]出错!", suiteName);
			json.setCode(ReturnJSONObject.SYSTEM_ERROR_CODE).setMsg(StrUtil.format("解析suite配置文件[{}.yaml]出错!", suiteName));
		}		
		
		return json;
	}
	
	/**
	 * 获取测试报告文件，默认为ztest报告处理器生成的html
	 * @param filename 要下载的测试报告文件名称，如果没有提供，则指定为当前的测试报告
	 * @return
	 */
	@RequestMapping(value = "/api/getReport", method = RequestMethod.GET)
	public ReturnJSONObject getReport(HttpServletRequest request, HttpServletResponse response) {
		ReturnJSONObject json = new ReturnJSONObject();

		String fileName = request.getParameter("filename");
		if (StrUtil.isEmpty(fileName)) {
			// 下载当前测试报告
			if (GlobalTestConfig.report == null || !GlobalTestConfig.report.isFinished()) {
				return json.setCode(ReturnJSONObject.FAIL_CODE).setMsg("当前没有可用的测试报告或者当前测试还未完成，请指定测试报告文件名或者等待当前测试完成");
			}

			if (!FileUtil.exist(GlobalTestConfig.ENV_INFO.getReportFolder() + "/"
					+ GlobalTestConfig.report.getReportName() + "_ztest.html")) {
				// 如果报告文件不存在则生成ztest报告
				new ZTestReportManager().manage(GlobalTestConfig.report);
			}
			fileName = GlobalTestConfig.report.getReportName() + "_ztest.html";
		}
		String reportFile = GlobalTestConfig.ENV_INFO.getReportFolder() + "/" + fileName;

		if (!FileUtil.exist(reportFile)) {
			return json.setCode(ReturnJSONObject.FAIL_CODE).setMsg(StrUtil.format("测试报告[{}]不存在", fileName));
		}

		TestKit.renderDownload(response, reportFile, "report.html");

		return null;
	}
	
}
