package ${javaPackage};

import com.dcits.yi.ui.element.BasePage;
import com.dcits.yi.ui.element.PageElement;

/**
 * This PageModel is generated by MasterYIUITest
 * @author xuwangcheng14@163.com
 */

public class ${className} extends BasePage {
	
	<#list properties as property>
	public PageElement ${property};	
	</#list>
	
	
}