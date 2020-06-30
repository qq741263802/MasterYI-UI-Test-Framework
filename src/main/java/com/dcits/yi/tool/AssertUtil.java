package com.dcits.yi.tool;

import cn.hutool.core.util.StrUtil;

/**
 * 断言工具
* @author xuwangcheng  
* @date 2018年11月11日  
* @version 1.0.0  
*
 */
public class AssertUtil {
	
	/**
	 * 断言是否为真
	 * @param expression 布尔值表达式
	 * @param errorMsgTemplate 错误抛出异常附带的消息模板，变量用{}代替
	 * @param params 参数列表
	 */
	public static void isTrue(boolean expression, String errorMsgTemplate, Object... params) {
		if (false == expression) {
			throw new AssertFailException(StrUtil.format(errorMsgTemplate, params));
		}
	}
	
	/**
	 * 断言是否为真
	 * @param expression 布尔值表达式
	 */
	public static void isTrue(boolean expression)  {
		isTrue(expression, "[断言失败] - 表达式结果必须为 True");
	}
	
	/**
	 * 断言是否为假
	 * @param expression 布尔值表达式
	 * @param errorMsgTemplate 错误抛出异常附带的消息模板，变量用{}代替
	 * @param params 参数列表
	 */
	public static void isFalse(boolean expression, String errorMsgTemplate, Object... params) {
		if (expression) {
			throw new AssertFailException(StrUtil.format(errorMsgTemplate, params));
		}
	}
	
	/**
	 * 断言是否为假
	 * @param expression 布尔值表达式
	 */
	public static void isFalse(boolean expression)  {
		isFalse(expression, "[断言失败] - 表达式结果必须为 False");
	}
	
	/**
	 * 断言对象是否为{@code null}
	 * @param object 被检查的对象 
	 * @param errorMsgTemplate 错误抛出异常附带的消息模板，变量用{}代替
	 * @param params 参数列表
	 */
	public static void isNull(Object object, String errorMsgTemplate, Object... params) {
		if (object != null) {
			throw new AssertFailException(StrUtil.format(errorMsgTemplate, params));
		}
	}
	
	/**
	 * 断言对象是否为{@code null}
	 * @param object 被检查的对象 
	 */
	public static void isNull(Object object) {
		isNull(object, "[断言失败] - 传入的对象参数必须为 Null");
	}
	
	/**
	 * 断言对象是否不为{@code null}
	 * @param object 被检查对象
	 * @param errorMsgTemplate 错误抛出异常附带的消息模板，变量用{}代替
	 * @param params 参数列表
	 */
	public static void notNull(Object object, String errorMsgTemplate, Object... params) {
		if (object == null) {
			throw new AssertFailException(StrUtil.format(errorMsgTemplate, params));
		}
	}
	
	/**
	 * 断言对象是否不为{@code null}
	 * @param object 被检查对象
	 */
	public static void notNull(Object object) {
		notNull(object, "[断言失败] - 传入的对象参数不能为 Null");
	}
	
	/**
	 * 检查给定字符串是否为空
	 * @param text 被检查字符串
	 * @param errorMsgTemplate 错误消息模板，变量使用{}表示
	 * @param params 参数
	 * @return 非空字符串
	 */
	public static String notEmpty(String text, String errorMsgTemplate, Object... params) {
		if (StrUtil.isEmpty(text)) {
			throw new AssertFailException(StrUtil.format(errorMsgTemplate, params));
		}
		return text;
	}
	
	/**
	 * 检查给定字符串是否为空
	 * @param text 被检查字符串
	 * @return 非空字符串
	 */
	public static String notEmpty(String text) {
		return notEmpty(text, "[断言失败] - 传入的字符串参数不能为空字符串或者Null");
	}
	
	/**
	 * 检查给定字符串是否为空白（null、空串或只包含空白符）
	 * @param text 被检查字符串
	 * @param errorMsgTemplate 错误消息模板，变量使用{}表示
	 * @param params 参数
	 * @return 非空字符串
	 */
	public static String notBlank(String text, String errorMsgTemplate, Object... params) {
		if (StrUtil.isBlank(text)) {
			throw new AssertFailException(StrUtil.format(errorMsgTemplate, params));
		}
		return text;
	}
	
	/**
	 * 检查给定字符串是否为空白（null、空串或只包含空白符）
	 * @param text 被检查字符串
	 * @return 非空字符串
	 */
	public static String notBlank(String text) {
		return notBlank(text, "[断言失败] - 传入的字符串参数不能为空字符/Null或者只包含空字符串");
	}
	
	/**
	 * 断言给定字符串是否不被另一个字符串包含（既是否为子串）
	 * @param textToSearch 被搜索的字符串
	 * @param substring 被检查的子串
	 * @param errorMsgTemplate 异常时的消息模板
	 * @param params 参数列表
	 * @return 被检查的子串
	 */
	public static String notContain(String textToSearch, String substring, String errorMsgTemplate, Object... params) {
		if (StrUtil.isNotEmpty(textToSearch) && StrUtil.isNotEmpty(substring) && textToSearch.contains(substring)) {
			throw new AssertFailException(StrUtil.format(errorMsgTemplate, params));
		}
		return substring;
	}
	
	/**
	 * 断言给定字符串是否不被另一个字符串包含（既是否为子串）
	 * @param textToSearch 被搜索的字符串
	 * @param substring 被检查的子串
	 * @return 被检查的子串
	 */
	public static String notContain(String textToSearch, String substring) {
		return notContain(textToSearch, substring, "[断言失败] - 字符串 [{}] 必须不包含 子字符串 [{}]", textToSearch, substring);
	}
	
	/**
	 * 断言给定字符串是否被另一个字符串包含（既是否为子串）
	 * @param textToSearch 被搜索的字符串
	 * @param substring 被检查的子串
	 * @param errorMsgTemplate 异常时的消息模板
	 * @param params 参数列表
	 * @return 被检查的子串
	 */
	public static String contain(String textToSearch, String substring, String errorMsgTemplate, Object... params) {
		if (StrUtil.isNotEmpty(textToSearch) && StrUtil.isNotEmpty(substring) && !textToSearch.contains(substring)) {
			throw new AssertFailException(StrUtil.format(errorMsgTemplate, params));
		}
		return substring;
	}
	
	/**
	 * 断言给定字符串是否被另一个字符串包含（既是否为子串）
	 * @param textToSearch 被搜索的字符串
	 * @param substring 被检查的子串
	 * @return 被检查的子串
	 */
	public static String contain(String textToSearch, String substring) {
		return contain(textToSearch, substring, "[断言失败] - 字符串 [{}] 必须包含 子字符串 [{}]", textToSearch, substring);
	}
	
	/**
	 * 检查值是否在指定范围内
	 * @param value 值
	 * @param min 最小值（包含）
	 * @param max 最大值（包含）
	 * @return 检查后的长度值
	 */
	public static Number checkBetween(Number value, Number min, Number max) {
		notNull(value);
		notNull(min);
		notNull(max);
		double valueDouble = value.doubleValue();
		double minDouble = min.doubleValue();
		double maxDouble = max.doubleValue();
		if (valueDouble < minDouble || valueDouble > maxDouble) {
			throw new AssertFailException(StrUtil.format("[断言失败] - 值  [{}] 必须小于等于  [{}] 并且大于等于  [{}]", value, max, min));
		}
		return value;
	}
	
	/**
	 * 断言两个对象是否相等
	 * @param o1 不能为null
	 * @param o2 不能为null
	 */
	public static void equals(Object o1, Object o2, String errorMsgTemplate, Object... params) {
		notNull(o1);
		notNull(o2);
		if (!o1.equals(o2)) {
			throw new AssertFailException(errorMsgTemplate, params);
		}
	}
	
	/**
	 * 断言两个对象是否相等
	 * @param o1 不能为null
	 * @param o2 不能为null
	 */
	public static void equals(Object o1, Object o2) {
		equals(o1, o2, StrUtil.format("[断言失败] - 值  [{}] 必须相等于  [{}]", o1.toString(), o2.toString()));
	}
	
	/**
	 * 断言两个对象是否不相等
	 * @param o1 不能为null
	 * @param o2 不能为null
	 */
	public static void notEquals(Object o1, Object o2, String errorMsgTemplate, Object... params) {
		notNull(o1);
		notNull(o2);
		if (o1.equals(o2)) {
			throw new AssertFailException(errorMsgTemplate, params);
		}
	}
	
	
	/**
	 * 断言两个对象是否不相等
	 * @param o1 不能为null
	 * @param o2 不能为null
	 */
	public static void notEquals(Object o1, Object o2) {
		notEquals(o1, o2, StrUtil.format("[断言失败] - 值  [{}] 必须不等于  [{}]", o1.toString(), o2.toString()));
	}
}
