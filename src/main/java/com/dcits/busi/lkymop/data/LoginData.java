package com.dcits.busi.lkymop.data;

import cn.hutool.setting.dialect.Props;
import com.dcits.yi.tool.TestKit;
import com.dcits.yi.ui.data.BaseDataModel;

public class LoginData  extends BaseDataModel{

    public  String send_username;
    public  String send_password;


    @Override
    public void initData() {



        Props p = new Props(TestKit.getProjectRootPath() + "/config/data/login.data");
        send_username=p.getStr("send_username");
        send_password=p.getStr("send_password");

    }

    @Override
    public void destroyData() {
        System.out.println("");
    }
}
