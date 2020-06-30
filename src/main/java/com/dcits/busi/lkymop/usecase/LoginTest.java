package com.dcits.busi.lkymop.usecase;

import com.dcits.busi.lkymop.data.LoginData;
import com.dcits.busi.lkymop.page.LoginPage;
import com.dcits.yi.ui.usecase.UseCase;

public class LoginTest {

    public LoginPage loginPage;
    public LoginData loginData;

    @UseCase(name = "MOP登录", tag = "login")
    public void loginTest() throws Exception {
        loginPage.open();
        loginPage.login(loginData.send_username, loginData.send_password);


    }
}



