package com.dcits.busi.lkymop.data;

import cn.hutool.setting.dialect.Props;
import com.dcits.yi.tool.TestKit;
import com.dcits.yi.ui.data.BaseDataModel;

public class UserData extends BaseDataModel {

    public  String send_username;

    @Override
    public void initData() {


        Props p = new Props(TestKit.getProjectRootPath() + "/config/data/ka-user.data");
        send_username=p.getStr("send_username");

    }

    @Override
    public void destroyData() {

    }
}
