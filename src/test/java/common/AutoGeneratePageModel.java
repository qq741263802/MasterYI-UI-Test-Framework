package common;

import java.io.File;
import java.util.Map;

import com.dcits.yi.tool.TestKit;

import cn.hutool.core.io.FileUtil;
import cn.hutool.extra.template.Engine;
import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateConfig.ResourceMode;
import cn.hutool.extra.template.TemplateUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;

/**
 * 根据原始定义的element.yaml自动生成PageModel类
 * @author xuwangcheng
 * @version 20181013
 *
 */
public class AutoGeneratePageModel {
	public static void main(String[] args) throws Exception {
		generate("mop-ka-users", "com.dcits.busi.lkymop.page");
	}
	
	@SuppressWarnings("rawtypes")
	public static void generate (String yamlFileName, String packageName) throws Exception {
		//读取yaml文件
		String path = TestKit.getProjectRootPath() + "/config/element/" + yamlFileName + ".yaml";
		if (!FileUtil.exist(path)) {
			throw new Exception("元素定义yaml文件存在，请检查  => " + path);
		}
		
		//确认包结构是否存在
		String packagePath = packageName.replaceAll("\\.", "/");
		packagePath = TestKit.getProjectRootPath() + "/src/main/java/" + packagePath;
		if (!FileUtil.exist(packagePath)) {
			throw new Exception("包结构不存在，你需要先手动创建 => " + packagePath);
		}
		
		Map map = TestKit.parseYaml(path);	
		
		Engine engine = TemplateUtil.createEngine(new TemplateConfig(TestKit.getProjectRootPath() + "/template", ResourceMode.FILE));
		Template template = engine.getTemplate("pageModelTemplate.ftl");
		
		
		//拼装对象,生成指定的java文件
		for (Object key:map.keySet()) {
			Map m = (Map) map.get(key);
			JSONObject pageModel = new JSONObject();
			pageModel.put("javaPackage", packageName);
			pageModel.put("className", key);
			JSONArray properties = new JSONArray();
			pageModel.put("properties", properties);
			for (Object elementName:m.keySet()) {
				if ("url".equalsIgnoreCase(elementName.toString()) || elementName.toString().endsWith("frame")) {
					continue;
				}
				properties.add(elementName.toString());
			}
			String str = template.render(pageModel);
			FileUtil.writeString(str, new File(packagePath + "/" + key + ".java"), "utf-8");
			System.out.println("PageModel对象 " + key + ".java 创建完成");
		}		
		
	}
}
