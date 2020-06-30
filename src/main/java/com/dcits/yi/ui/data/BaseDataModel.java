package com.dcits.yi.ui.data;

import java.util.List;
import java.util.Random;

import com.dcits.yi.tool.xeger.Xeger;
import com.dcits.yi.ui.GlobalTestConfig;
import com.dcits.yi.ui.data.db.ExecOperater;
import com.dcits.yi.ui.data.db.TestDB;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.csv.CsvData;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvRow;
import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;

/**
 * 数据模型基类
 * @author xuwangcheng
 * @version 20181008
 *
 */
public abstract class BaseDataModel {
	
	private static final Log logger = LogFactory.get();
	
	/**
	 * 是否为初次初始化该数据，可以通过此参数判断是否在initData方法中执行destroyData方法
	 */
	protected boolean firstInitFlag = true;
	
	/**
	 * 自定义初始化数据，如果数据需要更新，需要重新调用一次该方法
	 */
	public abstract void initData();
	
	/**
	 * 定义销毁数据的方法，可以测试过程中、initData之前手动调用，在测试结束之后，框架也会自动化调用该方法
	 */
	public abstract void destroyData();
	
	
	public void setFirstInitFlag(boolean firstInitFlag) {
		this.firstInitFlag = firstInitFlag;
	}
	
	public boolean isFirstInitFlag() {
		return firstInitFlag;
	}
	
	/**
	 * 从excel读取数据,默认第一个sheet页
	 * @param path
	 * @return 行的集合，一行使用List表示
	 */
	public List<List<Object>> readFromExcel(String path) {
		ExcelReader reader = ExcelUtil.getReader(path);
		return reader.read();
	}
	
	/**
	 * 从excel读取数据
	 * @param path
	 * @param sheetNum sheet序号，从0开始
	 * @return 行的集合，一行使用List表示
	 */
	public List<List<Object>> readFromExcel(String path, int sheetNum) {
		ExcelReader reader = ExcelUtil.getReader(path, sheetNum);
		return reader.read();
	}
	
	/**
	 * 从csv中读取内容
	 * @param path
	 * @return CsvRow对象表示一行数据，通过getRawList()方法获取行数据
	 */
	public List<CsvRow> readFromCsv(String path) {
		CsvReader reader = CsvUtil.getReader();
		//从文件中读取CSV数据
		CsvData data = reader.read(FileUtil.file(path));
		return data.getRows();
	}
	
	/**
	 * 从txt配置文件中读取数据，需要自行处理
	 * @param path
	 * @param charsetName
	 * @return
	 */
	public String readFromTxt(String path, String charsetName) {
		return FileUtil.readString(path, charsetName);
	}
	
	/**
	 * 从txt配置文件中读取数据，需要自行处理
	 * @param path
	 * @return
	 */
	public String readFromTxt(String path) {
		return FileUtil.readString(path, "utf-8");
	}
	
	/**
	 * 生成随机字符串
	 * 
	 * @param mode 模式 0-只包含大写字母   1-只包含小写字母   2-包含大小写字母   3-同时包含大小写字母和数字
	 * @param length 字符串长度
	 * @return
	 */
	public String generateRandomString(int mode, int length) {
		// 小写字母0-25 大写字母26-51 数字52-61
		String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		int count = 0;
		StringBuilder randomStr = new StringBuilder();
		while (count < length) {
			count++;
			switch (mode) {
			case 0:
				randomStr.append(str.charAt(generateRandomNum(51, 26)));
				break;
			case 1:
				randomStr.append(str.charAt(generateRandomNum(25, 0)));
				break;
			case 2:
				randomStr.append(str.charAt(generateRandomNum(51, 0)));
				break;
			case 3:
				randomStr.append(str.charAt(generateRandomNum(61, 0)));
				break;
			default:
				break;
			}
		}
		return randomStr.toString();

	}

	/**
	 * 获取随机数
	 * @param max 最大值         
	 * @param min 最小值
	 * @return
	 */
	public int generateRandomNum(int max, int min) {
		Random ran = new Random();
		return ran.nextInt(max) % (max - min + 1) + min;
	}
	
	/**
	 * 根据正则生成指定的值
	 * @param regexStr
	 * @return
	 */
	public String generateByRegex(String regexStr) {
		return new Xeger(regexStr).generate();
	}
	
	/**
	 * 执行sql并获取,返回多条信息只会取第一条
	 * @param dbName seleniumConfig.properties配置文件中定义的数据库名称
	 * @param sql
	 * @return
	 * @throws Exception 
	 */
	public String generateBySql(String dbName, String sql) throws Exception {
		TestDB db = GlobalTestConfig.dbConnections.get(dbName);
		if (db == null) {
			throw new Exception("不存在标识为" + dbName + "数据库信息!");
		}
		logger.info("执行sql:[{}], 数据库标识为{}", sql, dbName);
		return db.execSql(sql);
	}
	
	/**
	 * 自定义执行SQL
	 * @param dbName seleniumConfig.properties配置文件中定义的数据库名称
	 * @param oper
	 * @return
	 * @throws Exception 
	 */
	public void generateBySql(String dbName, ExecOperater oper) throws Exception {
		TestDB db = GlobalTestConfig.dbConnections.get(dbName);
		if (db == null) {
			throw new Exception("不存在标识为" + dbName + "数据库信息!");
		}
		logger.info("自定义执行sql, 数据库标识为{}", dbName);
		db.execSql(oper);
	}
	
	
	
}
