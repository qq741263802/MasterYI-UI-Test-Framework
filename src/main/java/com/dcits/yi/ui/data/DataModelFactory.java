package com.dcits.yi.ui.data;

import com.dcits.yi.ui.GlobalTestConfig;

import cn.hutool.core.lang.Singleton;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;

/**
 * 数据模型实例工厂
 * @author xuwangcheng
 * @version 20181012
 *
 */
public class DataModelFactory {
	private static final Log logger = LogFactory.get();
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T extends BaseDataModel> T getDataModelInstance(Class clazz) {
		try {
			BaseDataModel model = (BaseDataModel) Singleton.get(clazz);
			if (model.isFirstInitFlag()) {
				model.initData();
				model.setFirstInitFlag(false);
				GlobalTestConfig.getTestRunningObject().getDatas().add(model);
			}						
			return (T) model;
		} catch (Exception e) {
			logger.error(e, "创建数据模型实例失败![{}]", clazz.getName());
			return null;
		}		
	}
	
	/**
	 * 如果没有指定自定义的实现类，返回默认实现
	 * @return
	 */
	public static BaseDataModel getDataModelInstance() {
		return new DefaultDataModel();
	}
}
