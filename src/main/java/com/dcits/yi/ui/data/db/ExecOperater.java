package com.dcits.yi.ui.data.db;

import java.sql.Connection;

/**
 * 自定义SQL执行器
 * @author xuwangcheng
 * @version 20181012
 *
 */
public interface ExecOperater {
	/**
	 * 自定义执行sql
	 * @param conn
	 * @return
	 */
	String exec(Connection conn);
}
