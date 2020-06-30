package com.dcits.yi.ui.element.basics;

import java.util.Map;

import org.openqa.selenium.support.ui.Select;

import com.dcits.yi.ui.element.PageElement;

/**
 * 元素对象模型基类，包含一些基础方法
 * @author xuwangcheng
 * @version 20181011
 *
 */
public interface IBaseElement {
	/**
	 * 获取文本内容
	 * @return
	 */
	String getText();
	/**
	 * 获取元素属性值
	 * @param attributeName
	 * @return
	 */
	String getAttributeValue(String attributeName);
	
	/**
	 * 获取元素的标签名称
	 * @return
	 */
	String getTagName();
	
	/**
	 * 鼠标悬停
	 */
	void mouseHover();
	/**
	 * 鼠标右击
	 */
	void mouseRightClick();
	/**
	 * 鼠标双击
	 */
	void mouseDoubleClick();
	/**
	 * 鼠标点击
	 */
	void mouseClick();
	/**
	 * 点击
	 */
	void click();
	/**
	 * 元素是否存在
	 * @return
	 */
	boolean isExist();
	/**
	 * 拖拽一个元素
	 * @param begin
	 * @param end
	 */
	void mouseDragAndDrop(PageElement end);
	
	/**
	 * 滑动
	 * @param x x轴距离
	 * @param y y轴距离
	 */
	void swipe(int x, int y);
	
	/**
	 * 
	 * 	上传文件<br>
	 * 	1、在input的标签对中输入文件所在位置使用sendkeys<br>
	 * 	2、如果页面上传功能不是input实现的,可以参考使用autoit工具来实现上传 <a href="http://www.cnblogs.com/fnng/p/4188162.html">selenium借助AutoIt识别上传</a>
	 * @param filePath 文件绝对路径
	 */
	void upload(String filePath);
	
	/**
	   *    发送内容给输入框
	 * @param str  字符串、按键、文件路径等
	 */
	void sendKeys(String str);
	/**
	 * 发送内容给输入框
	 * @param str
	 * @param clearFlag 传入true则再发送之前清除内容
	 */
	void sendKeys(String str, boolean clearFlag);
	/**
	 * 清除文本框内容
	 */
	void clear();
	
	/**
	 * 获取Select对象
	 * @return
	 */
	Select getSelect();
	/**
	 * 根据下拉框选项的value值来选择
	 * @param value
	 */
	void selectByValue(String value);
	/**
	 * 根据下拉框选项的文本text来选择
	 * @param option
	 */
	void selectByOption(String option);
	/**
	 * 获取下拉框当前选中的值
	 * @return
	 */
	String getSelectedValue();
	/**
	 * 获取当前下拉框所有选项
	 * @return Map&lt;K, V&gt; K为value V为text
	 */
	Map<String, String> getAllOptions();
	
	/**
	 * 验证码识别
	 * @param pageEle 图片元素
	 * @param x   验证图片所在页面的x轴起始位置,px
	 * @param y   验证图片所在页面的y轴起始位置,px
	 * @param language 字库文件,不需要后缀,不传则默认为英文
	 * @return
	 * @throws Exception
	 */
	String OCRCode(int x, int y, String language);
}
