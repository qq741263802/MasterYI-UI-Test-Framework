package com.dcits.busi.lkymop.page;

import com.dcits.yi.ui.element.BasePage;
import com.dcits.yi.ui.element.PageElement;

/**
 * This PageModel is generated by MasterYIUITest
 *
 */

public class UserPage extends BasePage {
	
	public PageElement userlist;	
	public PageElement userquerybypage;


	public void usersend()
	{
		this.userlist.click();
		this.userquerybypage.click();
		screenshot();

	}
	
}