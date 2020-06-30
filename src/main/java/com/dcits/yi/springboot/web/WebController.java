package com.dcits.yi.springboot.web;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dcits.yi.springboot.ReturnJSONObject;
import com.dcits.yi.tool.TestKit;
import com.dcits.yi.ui.report.SuiteReport;
import com.dcits.yi.ui.report.manage.IReportManager;
import com.dcits.yi.ui.report.manage.ReportPersistenceReportManager;
import com.dcits.yi.ui.report.manage.ZTestReportManager;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;

/**
 * 
* @version 1.0.0
* @Description 
* @author xuwangcheng
* @date 2019年1月11日下午3:32:09
 */
@Controller
public class WebController {
	private static ReportPersistenceReportManager manager = new ReportPersistenceReportManager();
	private static final Log logger = LogFactory.get();
	
	@RequestMapping("/report/list")
	public String reportList(Map<String, Object> result) {
		List<Map<String, Object>> reports = manager.list();
		if (reports == null) {
			result.put("error", true);
		}
		result.put("reports", reports);
		return "reportList";
	}
	
	@ResponseBody
	@RequestMapping(value = "/report/download")
	public String download(HttpServletRequest request, HttpServletResponse response) {
		ReturnJSONObject json = new ReturnJSONObject();
		int id = Integer.valueOf(request.getParameter("id"));
		SuiteReport report = manager.get(id);
		
		if (report == null) {
			json.setCode(ReturnJSONObject.FAIL_CODE).setMsg("查询失败或者查询无结果!id={}", id);
			return JSONUtil.parse(json).toString();
		}
		ZTestReportManager ztestReportManager = new ZTestReportManager();
		String reportPath = ztestReportManager.createReportPath(report.getReportName());
		if (!FileUtil.exist(reportPath)) {
			ztestReportManager.manage(report);
		}
		
		TestKit.renderDownload(response, reportPath, "report.html");
		return null;
	}
	
	@ResponseBody
	@RequestMapping(value = "/report/manage")
	public String manage(HttpServletRequest request, HttpServletResponse response) {
		ReturnJSONObject json = new ReturnJSONObject();
		int id = Integer.valueOf(request.getParameter("id"));
		String managerClassName = request.getParameter("class");
		
		SuiteReport report = manager.get(id);
		
		if (report == null) {
			json.setCode(ReturnJSONObject.FAIL_CODE).setMsg("查询失败或者查询无结果!id={}", id);
			return JSONUtil.parse(json).toString();
		}
		
		String filePath = null;
		try {
			IReportManager reportManger = TestKit.parseReportManager(managerClassName);
			filePath = reportManger.manage(report);
		} catch (Exception e) {
			logger.error(e, "执行测试报告处理器[{}]失败！", managerClassName);
			
			json.setCode(ReturnJSONObject.SYSTEM_ERROR_CODE).setMsg("执行测试报告处理类[{}]失败！", managerClassName);
			return JSONUtil.parse(json).toString();			
		}
		
		if (filePath != null) {
			TestKit.renderDownload(response, filePath, "report." + FileUtil.extName(filePath));
			return null;
		}
		
		return "执行测试报告处理器[" + managerClassName + "]成功！";
	}
}
