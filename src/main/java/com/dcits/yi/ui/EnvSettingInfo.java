package com.dcits.yi.ui;

import java.io.File;
import java.util.Collection;

import com.dcits.yi.tool.TestKit;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.mail.MailAccount;
import cn.hutool.setting.dialect.Props;

/**
 * 测试环境信息
 * @author xuwangcheng
 * @version 20181012
 *
 */
public class EnvSettingInfo {
	
	public static boolean DEV_MODE = true;

	private String driverBinSuffix = "win".equals(TestKit.getOsName()) ? ".exe" : "";
	
	private boolean remoteMode;
	
	private String hubRemoteUrl;
	
	private String reportFolder = TestKit.getProjectRootPath() + "/report";
	private String screenshotFolder = "/screenshot";;
	
	private String elementFolder = TestKit.getProjectRootPath() + "/config/element/";
	private String suiteFolder = TestKit.getProjectRootPath() + "/config/suite/";
	
	private String chromeDriverPath = TestKit.getProjectRootPath() + "/src/main/resources/chromedriver" + driverBinSuffix;
	private String ieDriverPath = TestKit.getProjectRootPath() + "/src/main/resources/IEDriverServer" + driverBinSuffix;
	private String operaDriverPath = TestKit.getProjectRootPath() + "/src/main/resources/operadriver" + driverBinSuffix;
	private String firefoxDriverPath = TestKit.getProjectRootPath() + "/src/main/resources/geckodriver" + driverBinSuffix;
	
	private String firefoxBinPath = "";
	
	private Double defaultSleepSeconds;
	
	private Integer elementLocationRetryCount;
	private Double elementLocationTimeouts;
	
	/**
	 * 邮件配置信息
	 */
	private MailAccount mailAccount = new MailAccount();
	/**
	 * 收件人列表
	 */
	private Collection<String> tos;
	/**
	 * 抄送人列表
	 */
	private Collection<String> ccs;
	/**
	 * 密送人列表
	 */
	private Collection<String> bccs;
	
	/**
	 * 是否可以开启定时任务
	 */
	private boolean cronEnabled = false;
	
	/**
	 * 定时执行：suite文件
	 */
	private String cronSuite;
	
	/**
	 * 定时执行：cron表达式，支持linux crontab格式(5位)和Quartz的cron格式(6位)
	 */
	private String cronExpression;
	
	/**
	 * 存储测试报告数据的轻量级数据库，路径
	 */
	private String sqlitePath;
	/**
	 * OCR识别软件的路径
	 */
	private String tesseractOCRPath;
	
	public EnvSettingInfo() {
		super();
	}

	/**
	 * 初始化各环境变量
	 * @param props
	 */
	public EnvSettingInfo(Props props) {
		super();
		remoteMode = props.getBool("remote_mode", false);
		
		hubRemoteUrl = TestKit.getStrIsNotEmpty(props, "hub.remote.url", "http://127.0.0.1:4444/wd/hub");
		
		defaultSleepSeconds = props.getDouble("sleep_seconds", 0.5);
		
		elementLocationRetryCount = props.getInt("element_location_retry_count", 2);
		elementLocationTimeouts = props.getDouble("element_location_timeouts", 6.00);
		
		mailAccount.setHost(props.getStr("mail.host"));
		mailAccount.setPort(props.getInt("mail.port"));
		mailAccount.setAuth(true);
		mailAccount.setFrom(props.getStr("mail.from"));
		mailAccount.setUser(props.getStr("mail.username"));
		mailAccount.setPass(props.getStr("mail.passwd"));
		
		tos = CollUtil.newArrayList(props.getStr("mail.to").split(","));
		ccs = CollUtil.newArrayList(props.getStr("mail.cc").split(","));
		bccs = CollUtil.newArrayList(props.getStr("mail.bcc").split(","));

		cronEnabled = props.getBool("cron.enabled", false);
		cronSuite = props.getStr("cron.suitename", "");
		cronExpression = props.getStr("cron.expression", "");
		
		firefoxBinPath = props.getStr("firefox.bin.path", "");
		
		sqlitePath = TestKit.getStrIsNotEmpty(props, "sqlite_path", TestKit.getProjectRootPath() + File.separator + "report.db");
		
		tesseractOCRPath = props.getStr("tesseract_path", "");
		
		if (!EnvSettingInfo.DEV_MODE) {
			elementFolder =  TestKit.getProjectRootPath() + "/config/element/";
			suiteFolder =  TestKit.getProjectRootPath() + "/config/suite/";
			chromeDriverPath = TestKit.getProjectRootPath() + "/drivers/chromedriver" + driverBinSuffix;
			ieDriverPath = TestKit.getProjectRootPath() + "/drivers/IEDriverServer" + driverBinSuffix;
			operaDriverPath = TestKit.getProjectRootPath() + "/drivers/operadriver" + driverBinSuffix;
			firefoxDriverPath = TestKit.getProjectRootPath() + "/drivers/geckodriver" + driverBinSuffix;
		}
		
		if (StrUtil.isNotEmpty(chromeDriverPath)) {
			System.setProperty("webdriver.chrome.driver", chromeDriverPath);
		}
		if (StrUtil.isNotEmpty(ieDriverPath)) {
			System.setProperty("webdriver.ie.driver", ieDriverPath);
		}
		if (StrUtil.isNotEmpty(firefoxDriverPath)) {
			System.setProperty("webdriver.gecko.driver", firefoxDriverPath);
		}
		if (StrUtil.isNotEmpty(operaDriverPath)) {
			System.setProperty("webdriver.opera.driver", operaDriverPath);	
		}
		if (StrUtil.isNotEmpty(firefoxBinPath)) {
			System.setProperty("webdriver.firefox.bin", firefoxBinPath);
		}
	}
	
	public void setTesseractOCRPath(String tesseractOCRPath) {
		this.tesseractOCRPath = tesseractOCRPath;
	}
	
	public String getTesseractOCRPath() {
		return tesseractOCRPath;
	}
	
	public void setSqlitePath(String sqlitePath) {
		this.sqlitePath = sqlitePath;
	}
	
	public String getSqlitePath() {
		return sqlitePath;
	}
	
	public void setCronEnabled(boolean cronEnabled) {
		this.cronEnabled = cronEnabled;
	}
	
	public boolean isCronEnabled() {
		return cronEnabled;
	}
	
	public String getCronSuite() {
		return cronSuite;
	}

	public void setCronSuite(String cronSuite) {
		this.cronSuite = cronSuite;
	}

	public String getCronExpression() {
		return cronExpression;
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	public Integer getElementLocationRetryCount() {
		return elementLocationRetryCount;
	}

	public void setElementLocationRetryCount(Integer elementLocationRetryCount) {
		this.elementLocationRetryCount = elementLocationRetryCount;
	}

	public Double getElementLocationTimeouts() {
		return elementLocationTimeouts;
	}

	public void setElementLocationTimeouts(Double elementLocationTimeouts) {
		this.elementLocationTimeouts = elementLocationTimeouts;
	}

	public String getElementFolder() {
		return elementFolder;
	}

	public void setElementFolder(String elementFolder) {
		this.elementFolder = elementFolder;
	}

	public String getSuiteFolder() {
		return suiteFolder;
	}

	public void setSuiteFolder(String suiteFolder) {
		this.suiteFolder = suiteFolder;
	}

	public boolean isRemoteMode() {
		return remoteMode;
	}

	public void setRemoteMode(boolean remoteMode) {
		this.remoteMode = remoteMode;
	}

	public String getHubRemoteUrl() {
		return hubRemoteUrl;
	}

	public void setHubRemoteUrl(String hubRemoteUrl) {
		this.hubRemoteUrl = hubRemoteUrl;
	}

	public String getReportFolder() {
		return reportFolder;
	}

	public void setReportFolder(String reportFolder) {
		this.reportFolder = reportFolder;
	}

	public String getScreenshotFolder() {
		return screenshotFolder;
	}

	public void setScreenshotFolder(String screenshotFolder) {
		this.screenshotFolder = screenshotFolder;
	}

	public String getChromeDriverPath() {
		return chromeDriverPath;
	}

	public void setChromeDriverPath(String chromeDriverPath) {
		this.chromeDriverPath = chromeDriverPath;
	}

	public String getIeDriverPath() {
		return ieDriverPath;
	}

	public void setIeDriverPath(String ieDriverPath) {
		this.ieDriverPath = ieDriverPath;
	}

	public String getOperaDriverPath() {
		return operaDriverPath;
	}

	public void setOperaDriverPath(String operaDriverPath) {
		this.operaDriverPath = operaDriverPath;
	}

	public String getFirefoxDriverPath() {
		return firefoxDriverPath;
	}

	public void setFirefoxDriverPath(String firefoxDriverPath) {
		this.firefoxDriverPath = firefoxDriverPath;
	}

	public Double getDefaultSleepSeconds() {
		return defaultSleepSeconds;
	}

	public void setDefaultSleepSeconds(Double defaultSleepSeconds) {
		this.defaultSleepSeconds = defaultSleepSeconds;
	}

	public MailAccount getMailAccount() {
		return mailAccount;
	}

	public void setMailAccount(MailAccount mailAccount) {
		this.mailAccount = mailAccount;
	}

	public Collection<String> getTos() {
		return tos;
	}

	public void setTos(Collection<String> tos) {
		this.tos = tos;
	}

	public Collection<String> getCcs() {
		return ccs;
	}

	public void setCcs(Collection<String> ccs) {
		this.ccs = ccs;
	}

	public Collection<String> getBccs() {
		return bccs;
	}

	public void setBccs(Collection<String> bccs) {
		this.bccs = bccs;
	}

	@Override
	public String toString() {
		return "EnvSettingInfo [driverBinSuffix=" + driverBinSuffix + ", remoteMode=" + remoteMode + ", hubRemoteUrl="
				+ hubRemoteUrl + ", reportFolder=" + reportFolder + ", screenshotFolder=" + screenshotFolder
				+ ", elementFolder=" + elementFolder + ", suiteFolder=" + suiteFolder + ", chromeDriverPath="
				+ chromeDriverPath + ", ieDriverPath=" + ieDriverPath + ", operaDriverPath=" + operaDriverPath
				+ ", firefoxDriverPath=" + firefoxDriverPath + ", firefoxBinPath=" + firefoxBinPath
				+ ", defaultSleepSeconds=" + defaultSleepSeconds + ", elementLocationRetryCount="
				+ elementLocationRetryCount + ", elementLocationTimeouts=" + elementLocationTimeouts + ", mailAccount="
				+ mailAccount + ", tos=" + tos + ", ccs=" + ccs + ", bccs=" + bccs + ", cronEnabled=" + cronEnabled
				+ ", cronSuite=" + cronSuite + ", cronExpression=" + cronExpression + ", sqlitePath=" + sqlitePath
				+ ", tesseractOCRPath=" + tesseractOCRPath + "]";
	}
}
