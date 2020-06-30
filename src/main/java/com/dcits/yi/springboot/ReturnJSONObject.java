package com.dcits.yi.springboot;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;

/**
 * 默认返回json对象格式
* @version 1.0.0
* @Description 默认返回json对象格式
* @author xuwangcheng
* @date 2019年1月11日下午3:31:22
 */
public class ReturnJSONObject {
	
	public static final int SUCCESS_CODE = 200;
	public static final int SYSTEM_ERROR_CODE = 500;
	public static final int FAIL_CODE = 400;
	
	private int code = SUCCESS_CODE;
	private String msg = "";
	private Object data;
	
	public ReturnJSONObject put(String key, Object value) {
		if (data == null) {
			data = new JSONObject();
		}
	    ((JSONObject) data).put(key, value);
		return this;
	}
	
	public int getCode() {
		return code;
	}
	public ReturnJSONObject setCode(int code) {
		this.code = code;
		return this;
	}
	public String getMsg() {
		return msg;
	}
	public ReturnJSONObject setMsg(String msg, Object ... parameter) {
		this.msg = StrUtil.format(msg, parameter);
		return this;
	}

	public Object getData() {
		return data;
	}
	public ReturnJSONObject setData(Object data) {
		this.data = data;
		return this;
	}
	@Override
	public String toString() {
		return "ReturnJSONObject [code=" + code + ", msg=" + msg + ", data=" + data + "]";
	}
}	
