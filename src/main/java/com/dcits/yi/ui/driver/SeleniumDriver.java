package com.dcits.yi.ui.driver;

import java.net.MalformedURLException;
import java.net.URL;

import com.dcits.yi.tool.TestKit;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.dcits.yi.constant.TestConst;
import com.dcits.yi.ui.GlobalTestConfig;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;

/**
 * 管理不同类型WebDriver的初始化
 * @author xuwangcheng
 * @version 2018.09.25
 *
 */
public class SeleniumDriver {
	
	private static final Log logger = LogFactory.get();

	public static WebDriver initWebDriver(String browserName) throws MalformedURLException {
		logger.info("初始化指定类型的WebDriver[{} for {}]", browserName, TestKit.getOsName());
		WebDriver driver = null;
		//是否分布式执行	
		if (GlobalTestConfig.ENV_INFO.isRemoteMode()) {	
			driver = initRemoteDriver(browserName);
		} else {
			driver =  initWebdriver(browserName);
		}
		//driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		driver.manage().window().maximize();
		return driver;
	}
	
	private static WebDriver initWebdriver(String browserName) {
		WebDriver driver = null;
		switch (browserName.toLowerCase()) {
		case TestConst.BROWSER_CHROME:
			driver = new ChromeDriver();
			break;
		case TestConst.BROWSER_IE:
			driver = new InternetExplorerDriver();
			break;
		case TestConst.BROWSER_FIREFOX:
			driver = new FirefoxDriver();
			break;
		case TestConst.BROWSER_OPERA:
			driver = new OperaDriver();
			break;
		case TestConst.BROWSER_HTMLUNIT:
			driver = new HtmlUnitDriver();
			break;
		default:
			driver = new ChromeDriver();
			break;
		}
		return driver;
	}
	
	private static WebDriver initRemoteDriver(String browserName) throws MalformedURLException {
		WebDriver driver = null;
		DesiredCapabilities capabilities = null;
		switch (browserName.toLowerCase()) {
		case TestConst.BROWSER_CHROME:
			capabilities = DesiredCapabilities.chrome();
			break;
		case TestConst.BROWSER_IE:
			capabilities = DesiredCapabilities.internetExplorer();
			break;
		case TestConst.BROWSER_FIREFOX:
			capabilities = DesiredCapabilities.firefox();
			break;
		case TestConst.BROWSER_OPERA:
			capabilities = DesiredCapabilities.operaBlink();
			break;
		case TestConst.BROWSER_HTMLUNIT:
			capabilities = DesiredCapabilities.htmlUnit();
			break;
		default:
			capabilities = DesiredCapabilities.chrome();
			break;
		}
		driver = new RemoteWebDriver(new URL(GlobalTestConfig.ENV_INFO.getHubRemoteUrl()), capabilities);
		return driver;
	}
}
