package com.dcits.yi.ui.element;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

import com.dcits.yi.constant.TestConst;
import com.dcits.yi.tool.TestKit;
import com.dcits.yi.ui.GlobalTestConfig;
import com.dcits.yi.ui.element.basics.IBaseElement;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.util.ImageHelper;

/**
 * 页面元素对象基类
 * @author xuwangcheng
 * @version 20181012
 *
 */
public class PageElement extends BaseObject implements IBaseElement {
	private static final Log logger = LogFactory.get();
	
	protected WebElement ele;	
	protected Locator locator = new Locator();

	protected String name;
	
	public void setLocator(Locator locator) {
		this.locator = locator;
	}
	
	public Locator getLocator() {
		return locator;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	/**
	 * 传入替换占位符的参数
	 * @param params
	 */
	public PageElement setParams(Object... params) {
		if (locator.getPlaceholderParamsCount() >= 1) {
			locator.setLocationValue(StrUtil.format(locator.getLocationValue(), params));
		}				
		return this;
	}
	
	/**
	 * 获取WebElement对象
	 * @return
	 */
	protected WebElement getEle() {
		ele = null;	
		getStepReport().setElementName(getName());
		getStepReport().setLocation(StrFormatter.format("{} => {}{}", locator.getLocationType(), locator.getLocationValue()
				, (locator.getLocationSeq() > 0 ? "[" + locator.getLocationSeq() + "]" : "")));
		
		try {	
			ele = locator.getElement(getDriver());			
		} catch (Exception e) {
			logger.error("无法定位到元素 [{}][{} => {}{}],  当前Frame：{}", getName(), locator.getLocationType(), locator.getLocationValue()
					, (locator.getLocationSeq() > 0 ? "[" + locator.getLocationSeq() + "]" : "")
					, GlobalTestConfig.getTestRunningObject().getCurrentFrameName());
			getStepReport().setMark(StrFormatter.format("元素定位失败:{} => {}{}\n", locator.getLocationType(), locator.getLocationValue()
					, (locator.getLocationSeq() > 0 ? "[" + locator.getLocationSeq() + "]" : "")));
		}
		
		sleep(GlobalTestConfig.ENV_INFO.getDefaultSleepSeconds());
		return ele;
	}

	@Override
	public String OCRCode(int x, int y, String language) {
		String result = null;
		getEle();
		Dimension eleSize = ele.getSize();
		String capturePath = TestKit.getProjectRootPath() + BasePage.screenshot();
		if (!FileUtil.exist(capturePath)) {
			throw new RuntimeException("无法获取到页面截图!");
		}
		try {
			BufferedImage originalImage = ImageIO.read(new File(capturePath));
			BufferedImage croppedImage = originalImage.getSubimage(x, y, eleSize.getWidth(), eleSize.getHeight());
			String tempImageName = IdUtil.fastUUID() + ".png";
			ImageIO.write(ImageHelper.convertImageToBinary(croppedImage), "png", FileUtil.touch(TestConst.TEST_TEMP_FLODER + File.separator + tempImageName));
			
			File imageFile = new File(TestConst.TEST_TEMP_FLODER + File.separator + tempImageName);
			
			ITesseract instance = new Tesseract();
			//tesseract-ocr目录
			instance.setDatapath(GlobalTestConfig.ENV_INFO.getTesseractOCRPath() + File.separator  + "tessdata");
			instance.setLanguage(language);
			result = instance.doOCR(imageFile);
		} catch (Exception e) {
			logger.error(e, "验证码识别出错!");
			throw new RuntimeException("验证码识别出错!", e);
		}
		
		if (StrUtil.isNotEmpty(result)) {
			result = result.replaceAll("[^a-z^A-Z^0-9]", "");
		}
		
		return result;
		
	}
	
	@Override
	public String getText() {
		getEle();
		String result = ele.getText();
		getStepReport().setResult(result);		
		return result;
	}

	@Override
	public String getAttributeValue(String attributeName) {
		getEle();	
		String result = ele.getAttribute(attributeName);
		getStepReport().setResult(result);
		return result;
	}

	@Override
	public String getTagName() {
		getEle();
		String result = ele.getTagName();
		getStepReport().setResult(result);
		return result;
	}

	@Override
	public void mouseHover() {
		getEle();
		new Actions(getDriver()).moveToElement(ele).perform();
	}

	@Override
	public void mouseRightClick() {
		getEle();
		new Actions(getDriver()).contextClick(ele).perform();
		
	}

	@Override
	public void mouseDoubleClick() {
		getEle();
		new Actions(getDriver()).doubleClick(ele).perform();
	}

	@Override
	public boolean isExist() {
		getEle();
		if (ele == null) {
			return false;
		}
		return true;
	}

	@Override
	public void mouseDragAndDrop(PageElement end) {
		getEle();
		new Actions(getDriver()).dragAndDrop(ele, end.getEle()).perform();
	}

	@Override
	public void swipe(int x, int y) {
		getEle();
		new Actions(getDriver()).dragAndDropBy(ele, x, y);
	}

	@Override
	public void upload(String filePath) {
		getEle();
		ele.sendKeys(filePath);
	}

	@Override
	public void mouseClick() {
		getEle();
		new Actions(getDriver()).click(ele).perform();	
	}

	@Override
	public void click() {
		getEle();
		ele.click();
	}
	
	@Override
	public void sendKeys(String str) {
		getEle();
		ele.sendKeys(str);
		
	}

	@Override
	public void clear() {
		getEle();
		ele.clear();
	}

	@Override
	public void sendKeys(String str, boolean clearFlag) {
		getEle();
		if (clearFlag) {
			ele.clear();
		}
		ele.sendKeys(str);		
	}
	
	@Override
	public Select getSelect() {
		getEle();
		return new Select(ele);
	}

	@Override
	public void selectByValue(String value) {
		getSelect().selectByValue(value);
	}

	@Override
	public void selectByOption(String option) {
		getSelect().selectByVisibleText(option);
	}

	@Override
	public String getSelectedValue() {
		getEle();
		String str = ele.getAttribute("value");
		getStepReport().setResult(str);
		return str;
	}

	@Override
	public Map<String, String> getAllOptions() {
		List<WebElement> selectEles = getSelect().getAllSelectedOptions();
		Map<String, String> map = new HashMap<String, String>(selectEles.size());
		for (WebElement e:selectEles) {
			map.put(e.getAttribute("value"), e.getText());
		}
		getStepReport().setResult(map.toString());
		return map;
	}

	@Override
	public String toString() {
		return "PageElement [locator=" + locator + ", name=" + name + "]";
	}

}
