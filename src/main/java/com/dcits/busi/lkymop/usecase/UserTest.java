package com.dcits.busi.lkymop.usecase;

import com.dcits.busi.lkymop.page.UserPage;
import com.dcits.yi.ui.usecase.UseCase;

public class UserTest {

    public UserPage userPage;

    @UseCase(name = "员工管理", tag = "user")
    public void userTest() throws Exception {

        userPage.usersend();


    }

}


