package com.dcits.busi.mail.usecase;

import com.dcits.busi.mail.data.MailTestData;
import com.dcits.busi.mail.page.LoginPage;
import com.dcits.busi.mail.page.MailLogoutPage;
import com.dcits.busi.mail.page.MailPage;
import com.dcits.yi.ui.usecase.UseCase;

/**
 * 
* @version 1.0.0
* @Description 
* @author xuwangcheng
* @date 2019年1月11日下午3:31:04
 */
public class MailTest {

	public LoginPage loginPage;
	public MailPage mailPage;
	public MailLogoutPage logoutPage;
	
	public MailTestData data;
	
	@UseCase(name="163邮箱测试", tag="mail")
	public void mailTest() throws Exception {
		loginPage.open();
		loginPage.登录(data.send_email, data.send_password);
		mailPage.verifyLogin(data.send_email);
		mailPage.sendEmail(data.receive_email, "测试", data.send_content);
		mailPage.logout();
		logoutPage.verifyLogout();
		
		logoutPage.重新登录按钮.click();
		
		loginPage.登录(data.receive_email, data.receive_password);
		mailPage.verifyLogin(data.receive_email);
		mailPage.reveiceEmail(data.send_email);
		mailPage.replyEmail(data.send_content);
		mailPage.logout();
		logoutPage.verifyLogout();
	}
}
