package com.dcits.yi.ui.element.basics;

/**
 * 	页面模型接口类
 * @author xuwangcheng
 * @version 20181011
 *
 */
public interface IBasePage {
	/**
	 * 打开页面url地址
	 */
	void open();
	/**
	 * 打开指定的url地址
	 * @param url
	 */
	void open(String url);
	/**
	 * 获取当前窗口标题
	 * @return
	 */
	String getTitle();
	/**
	 * 获取当前浏览器地址栏地址
	 * @return
	 */
	String getCurrentUrl();
	/**
	 * 关闭该窗口
	 */
	void close();
	/**
	 * 刷新当前页面
	 */
	void refresh();
	/**
	 * 前进
	 */
	void forward();
	/**
	 * 后退
	 */
	void back();
	/**
	 * 跳转到指定的url地址
	 * @param url
	 */
	void to(String url);
	/**
	 * 	X掉关闭或者取消掉对话框
	 */
	void dialogDismiss();
	/**
	 * 	点击对话框的确认按钮
	 */
	void dialogAccept();	
	/**
	 * 	获取对话框中文本并点击确认
	 * @return
	 */
	String getDialogText();
	/**
	 * 	发送内容给prompt(文本对话框)
	 * @param keys
	 */
	void sendKeyDialog(String keys);
	/**
	   *   切换窗口
	 * @param index 窗口下标，从左到右，起始为0
	 */
	void switchWindow(int index);
	/**
	 * 执行js代码
	 * @param js
	 */
	void executeScript(String js);
	
	/**
	 * 使用AutoIt的脚本来实现上传
	 * @param executeScriptExe 由AutoIt生成或编写上传文件自动化脚本执行程序
	 */
	void uploadByAutoIt(String executeScriptExe);
}
