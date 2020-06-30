package com.dcits.yi.ui.element;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import com.dcits.yi.tool.TestKit;
import com.dcits.yi.ui.GlobalTestConfig;
import com.dcits.yi.ui.aop.CreateStepReportAspect;
import com.dcits.yi.ui.element.basics.IBasePage;

import cn.hutool.aop.ProxyUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;

/**
 * 页面对象模型
 * @author xuwangcheng
 *
 */
public class BasePage extends BaseObject implements IBasePage {
	private static final Log logger = LogFactory.get();
	
	/**
	 * 当前页面的url,非必须，实际上在测试过程中可以实时传入
	 */
	protected String url;
	
	@Deprecated
	protected boolean initFlag = false;
	
	/**
	 * 追加内容到当前操作步骤的备注信息
	 * @param format
	 * @param arguments
	 */
	public void Mark(String format, Object ... arguments) {
		if (getStepReport() != null) {
			getStepReport().setMark(getStepReport().getMark() + "\n" + StrUtil.format(format, arguments));
		}
	}
	
	/**
	 * 当前页面的截图,截图失败不会中断测试<br>
	 * 测试过程中如果发生异常,也会自动截图
	 */
	public static String screenshot() {
		String pngName = IdUtil.fastUUID() +".png";
		String capturePath = GlobalTestConfig.ENV_INFO.getScreenshotFolder() + "/" + pngName;		
		try {
			File screenShotFile = ((TakesScreenshot)GlobalTestConfig.getTestRunningObject().getDriver()).getScreenshotAs(OutputType.FILE); 
			FileUtils.copyFile(screenShotFile, new File(TestKit.getProjectRootPath() + capturePath));
			GlobalTestConfig.getTestRunningObject().getStepReport().setScreenshot(capturePath);
			logger.info("截图成功 => {}", capturePath);
			return capturePath;
		} catch (Exception e) {
			logger.error("截图过程中出错了 => {}", e.getMessage());
		}	
		return null;
	}
	
	/**
	 * 初始化该页面对象，自动完成页面中各元素对象的初始化
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public void initPageObject () throws Exception {
		//获取页面对应的element元素Map		
		String pageName = this.getClass().getSimpleName().split("\\$\\$")[0];
		Map map = MapUtil.get(GlobalTestConfig.elements, pageName, Map.class);
		if (map == null) {
			throw new Exception(pageName + "页面没有对应的Elelement配置信息，请检查element配置的yaml文件!"); 
		}
		
		//设置url
		if (map.get("url") != null) {
			this.url = map.get("url").toString();
		}
		
		//初始化页面下的元素对象
		Field[] fields = this.getClass().getFields();
		for (Field f:fields) {
			f.setAccessible(true);
			if (f.get(this) == null) {
				f.set(this, ProxyUtil.proxy(ReflectUtil.newInstance(f.getType()), CreateStepReportAspect.class));
			}
			if (f.get(this) instanceof PageElement && map.get(f.getName()) != null) {									
					((PageElement) f.get(this)).setLocator(new Locator(f.getName(), map.get(f.getName()).toString(),map));			
					((PageElement) f.get(this)).setName(f.getName());
				}
			}
		initFlag = true;
	}

	
	

	@Override
	public void open() {
		getStepReport().setParams(url);
		getDriver().get(url);
	}
	
	@Override
	public void close() {
		getDriver().close();
	}
	
	@Override
	public void refresh() {
		getDriver().navigate().refresh();
	}


	@Override
	public void forward() {
		getDriver().navigate().forward();
	}


	@Override
	public void back() {
		getDriver().navigate().back();
	}


	@Override
	public void to(String url) {
		getDriver().navigate().to(url);
	}

	@Override
	public void open(String url) {
		getDriver().get(url);
	}

	@Override
	public void dialogDismiss() {
		Alert alert = getDriver().switchTo().alert();
		alert.dismiss();		
	}

	@Override
	public void dialogAccept() {
		Alert alert = getDriver().switchTo().alert();
		alert.accept();
	}

	@Override
	public String getDialogText() {
		Alert alert = getDriver().switchTo().alert();
		String str = alert.getText();
		alert.accept();
		getStepReport().setResult(str);
		return str;
	}

	@Override
	public void sendKeyDialog(String keys) {
		Alert alert = getDriver().switchTo().alert();
		alert.sendKeys(keys);
		alert.accept();
	}

	@Override
	public String getTitle() {
		String result = getDriver().getTitle();
		getStepReport().setResult(result);
		return result;
	}

	@Override
	public String getCurrentUrl() {
		String result = getDriver().getCurrentUrl();
		getStepReport().setResult(result);
		return result;
	}

	@Override
	public void switchWindow(int index) {
		List<String> handles = new ArrayList<String>(getDriver().getWindowHandles());
		getDriver().switchTo().window(handles.get(index));		
	}

	@Override
	public void executeScript(String js) {
		((JavascriptExecutor) getDriver()).executeScript(js);		
	}

	@Override
	public void uploadByAutoIt(String executeScriptExe) {
		try {
			Process pro = Runtime.getRuntime().exec(executeScriptExe);
			pro.waitFor();
		} catch (Exception e) {
			logger.error(e, "使用AutoIt上传文件出错[{}]", executeScriptExe);
			throw new RuntimeException(StrUtil.format("使用AutoIt上传文件出错[{}]", executeScriptExe));
		}
	}
}
