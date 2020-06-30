package com.dcits.yi.constant;

import java.util.HashMap;
import java.util.Map;

import com.dcits.yi.tool.TestKit;

/**
 * 框架常量
 * @author xuwangcheng
 * @version 20181012
 *
 */
public class TestConst {
	
	public static final String FRAMEWORK_VERSION = "0.4.1beta";

	/**
	 * 测试缓存文件夹
	 */
	public static final String TEST_TEMP_FLODER = TestKit.getProjectRootPath() + "/temp";
	
	/**
	 * 浏览器类型:chrome
	 */
	public static final String BROWSER_CHROME = "chrome";
	/**
	 * 浏览器类型:IE
	 */
	public static final String BROWSER_IE = "ie";
	/**
	 * 浏览器类型:FireFox
	 */
	public static final String BROWSER_FIREFOX = "firefox";
	/**
	 * 浏览器类型:Opera
	 */
	public static final String BROWSER_OPERA = "opera";
	/**
	 * 浏览器类型:内置浏览器
	 */
	public static final String BROWSER_HTMLUNIT = "htmlunit";
	
	/**
	 * 测试报告处理器的默认包路径
	 */
	public static final String REPORT_MANAGER_PACKAGE = "com.dcits.yi.ui.report.manage.";
		
	/**
	 * 对应操作的中文释义，主要在日志记录和报告中使用，只有在此定义的方法名在执行过程中才会当成单个测试步骤进行报告记录<br>
	 * 并且再此定义的操作也会在前置后置过程中做一些其他的操作
	 */
	public static final Map<String, String> ACTION_KEYWORD = new HashMap<String, String>();
	
	static {
		ACTION_KEYWORD.put("getText", "获取元素文本");
		ACTION_KEYWORD.put("getAttributeValue", "获取元素属性值");
		ACTION_KEYWORD.put("getTagName", "获取元素标签名");
		ACTION_KEYWORD.put("mouseHover", "鼠标悬停");
		ACTION_KEYWORD.put("mouseRightClick", "鼠标右击");
		ACTION_KEYWORD.put("mouseDoubleClick", "鼠标双击");
		ACTION_KEYWORD.put("mouseDragAndDrop", "元素拖拽");
		ACTION_KEYWORD.put("swipe", "滑动");
		ACTION_KEYWORD.put("upload", "上传文件");
		ACTION_KEYWORD.put("mouseClick", "鼠标左击");
		ACTION_KEYWORD.put("switchWindow", "切换到指定窗口");

		ACTION_KEYWORD.put("click", "点击");
		
		ACTION_KEYWORD.put("sendKeys", "输入");
		ACTION_KEYWORD.put("clear", "清除文本");
			
		ACTION_KEYWORD.put("getTitle", "获取当前窗口标题");
		ACTION_KEYWORD.put("getCurrentUrl", "获取当前浏览器地址栏地址");
		ACTION_KEYWORD.put("open", "打开Url地址");
		ACTION_KEYWORD.put("close", "关闭当前窗口");
		ACTION_KEYWORD.put("refresh", "刷新页面");		
		ACTION_KEYWORD.put("forward", "由当前页面前进");
		ACTION_KEYWORD.put("back", "由当前页面后退");
		ACTION_KEYWORD.put("to", "跳转至");
		
		ACTION_KEYWORD.put("uploadByAutoIt", "使用AutoIt上传文件");
		
		ACTION_KEYWORD.put("dialogDismiss", "关闭弹出框");
		ACTION_KEYWORD.put("dialogAccept", "确认弹出框");
		ACTION_KEYWORD.put("getDialogText", "获取弹出框文本并确认");
		ACTION_KEYWORD.put("sendKeyDialog", "发送内容到文本对话框并确认");
		
		ACTION_KEYWORD.put("selectByValue", "根据value值选择下拉");
		ACTION_KEYWORD.put("selectByOption", "根据文本值选择下拉");
		ACTION_KEYWORD.put("getSelectedValue", "获取当前选中内容");
		ACTION_KEYWORD.put("getAllOptions", "获取全部下拉内容");
		
		ACTION_KEYWORD.put("OCRCode", "验证码识别");
	}
	
	/**
	 * 默认的frame名称
	 */
	public static final String DEFAULT_FRAME_NAME = "masterYiFrame";
}
