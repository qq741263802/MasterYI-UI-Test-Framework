package com.dcits.yi.ui.element;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.dcits.yi.constant.TestConst;
import com.dcits.yi.ui.GlobalTestConfig;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;

/**
 * 元素定位器
 * @author xuwangcheng
 * @version 20181012
 *
 */
public class Locator {
	private static final Log logger = LogFactory.get();
	
	/**
	 * 元素的名称
	 */
	private String name;
	/**
	 * 定位类型：Id、LinkText、Name、TagName、XPath、ClassName、PartialLinkText、CssSelector
	 */
	private String locationType;
	/**
	 * 参数值
	 */
	private String locationValue;
	/**
	 * 取值顺序
	 */
	private int locationSeq = 0;
	/**
	 * 占位符个数
	 */
	private int placeholderParamsCount = 0;
	/**
	 * 该元素所在frame的定位器集合,嵌套frame按照顺序
	 */
	private List<Locator> frameLocators = new ArrayList<Locator>();
	
	public Locator(String locationType, String locationValue, int locationSeq) {
		super();
		this.locationType = locationType;
		this.locationValue = locationValue;
		this.locationSeq = locationSeq;
	}

	public Locator() {
		super();
	}
	
	@SuppressWarnings("rawtypes")
	public Locator(String name, String info, Map elementMap) {
		super();
		String[] infos = info.split("\\s+");
		
		if (ReUtil.contains("'(.*?)'", info)) {
			String value = ReUtil.get("'(.*?)'", info, 1);
			info = info.replace(value, "default");
			infos = info.split("\\s+");
			infos[1] = value;
		}

		this.name = name;
		locationType = infos[0];
		locationValue = infos[1];
		//xpath最后面如果有中括号为xpath路径自带
		if (!"xpath".equalsIgnoreCase(locationType) && locationValue.endsWith("]")) {
			String seq = locationValue.substring(locationValue.indexOf("[") + 1, locationValue.indexOf("]"));
			if (NumberUtil.isInteger(seq)) {
				locationSeq = Integer.valueOf(seq);
			}
			locationValue = locationValue.substring(0, locationValue.indexOf("["));
		}	
		
		placeholderParamsCount = ReUtil.count("\\{\\}", locationValue);
		
		//如果元素处于frame内
		if (infos.length > 2) {
			String[] names = StrUtil.split(infos[2], "|");
			for (String n:names) {
				if (elementMap.get(n) == null) {
					continue;
				}
				
				Locator l = new Locator(n, elementMap.get(n).toString(), elementMap);
				frameLocators.add(l);
			}
		}
	}

	/**
	 * 获取元素，如果在指定时间内N次都没有获取到，则报错，默认次数为2
	 * @param driver
	 * @return
	 * @throws Exception
	 */
	public WebElement getElement(WebDriver driver) throws Exception {
		//检查元素所在frame和当前的frame是否一致
		if (frameLocators.size() > 0 && !CollUtil.getLast(frameLocators).getName().equals(GlobalTestConfig.getTestRunningObject().getCurrentFrameName())) {
			GlobalTestConfig.getTestRunningObject().switchFrame(null);
			for(Locator l:frameLocators) {
				GlobalTestConfig.getTestRunningObject().switchFrame(l);
			}
		} 
		
		if (frameLocators.size() == 0 && !TestConst.DEFAULT_FRAME_NAME.equals(GlobalTestConfig.getTestRunningObject().getCurrentFrameName())) {
			GlobalTestConfig.getTestRunningObject().switchFrame(null);
		}
		
		WebElement ele = null;
		By by = getBy();
		int count = GlobalTestConfig.ENV_INFO.getElementLocationRetryCount();
		long parttime = (long) (GlobalTestConfig.ENV_INFO.getElementLocationTimeouts() * 1000 / count);
		for (int i = 0;i < count; i++) {
			if (checkElementLoad(driver, by, parttime)) {
				if (locationSeq > 0) {
					ele = driver.findElements(by).get(locationSeq - 1);
				} else {
					ele = driver.findElement(by);
				}
				continue;
			} else {
				logger.warn("Cannot Location Element [{}] [{} => {}]", this.name, this.locationType, this.locationValue);
			}
		}
		if (ele == null) {
			throw new RuntimeException(StrUtil.format("Cannot Location Element [{}] [{} => {}]", this.name, this.locationType, this.locationValue));
		}
		return ele;
	}
	
	/**
	 * 检查元素是否存在于页面的DOM中，但是可能该元素不是可见的
	 * @param driver
	 * @param by
	 * @param timeout
	 * @return
	 */
	private boolean checkElementLoad(WebDriver driver, By by, long timeout) {
		try {
			long seconds = timeout/1000;
			new WebDriverWait(driver, seconds).until(ExpectedConditions.presenceOfAllElementsLocatedBy(by));
            return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * 通过反射获取指定类型的By实例
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private By getBy() throws Exception {
		Class clz = Class.forName("org.openqa.selenium.By$By" + BaseObject.LOCATION_TYPES.get(locationType.toLowerCase().trim()));
		Constructor ctr = clz.getConstructor(String.class);
		return (By) ctr.newInstance(locationValue);
	}
	
	public void setFrameLocators(List<Locator> frameLocators) {
		this.frameLocators = frameLocators;
	}
	
	public List<Locator> getFrameLocators() {
		return frameLocators;
	}
	
	public String getLocationType() {
		return locationType;
	}

	public void setLocationType(String locationType) {
		this.locationType = locationType;
	}

	public String getLocationValue() {
		return locationValue;
	}

	public void setLocationValue(String locationValue) {
		this.locationValue = locationValue;
	}

	public int getLocationSeq() {
		return locationSeq;
	}

	public void setLocationSeq(int locationSeq) {
		this.locationSeq = locationSeq;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setPlaceholderParamsCount(int placeholderParamsCount) {
		this.placeholderParamsCount = placeholderParamsCount;
	}
	
	public int getPlaceholderParamsCount() {
		return placeholderParamsCount;
	}

	@Override
	public String toString() {
		return "Locator [locationType=" + locationType + ", locationValue=" + locationValue + ", locationSeq="
				+ locationSeq + "]";
	}
}
