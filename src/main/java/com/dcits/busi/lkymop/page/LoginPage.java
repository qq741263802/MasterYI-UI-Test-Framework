package com.dcits.busi.lkymop.page;

import com.dcits.yi.ui.element.BasePage;
import com.dcits.yi.ui.element.PageElement;

/**
 * This PageModel is generated by MasterYIUITest
 *
 */

public class LoginPage extends BasePage {
	
	public PageElement username;	
	public PageElement password;	
	public PageElement submit;	
	
	public void login(String username,String password)
	{
      this.username.sendKeys(username);
      this.password.sendKeys(password);
      this.submit.click();
      screenshot();


	}
}