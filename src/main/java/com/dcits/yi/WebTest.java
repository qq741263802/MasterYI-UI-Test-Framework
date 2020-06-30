package com.dcits.yi;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.openqa.selenium.WebDriver;

import com.dcits.yi.constant.TestConst;
import com.dcits.yi.tool.TestKit;
import com.dcits.yi.ui.GlobalTestConfig;
import com.dcits.yi.ui.aop.CreateStepReportAspect;
import com.dcits.yi.ui.data.BaseDataModel;
import com.dcits.yi.ui.data.DataModelFactory;
import com.dcits.yi.ui.element.BasePage;
import com.dcits.yi.ui.report.SuiteReport;
import com.dcits.yi.ui.report.manage.IReportManager;
import com.dcits.yi.ui.usecase.ExecuteCaseModel;
import com.dcits.yi.ui.usecase.UseCase;

import cn.hutool.aop.ProxyUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.convert.ConverterRegistry;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;

/**
 * 单次执行
 * @author xuwangcheng
 * @version 20181012
 *
 */
public class WebTest {	
	private static final Log logger = LogFactory.get();
	
	private String testTitle = "Web自动化";
	
	private Set<String> browserType = CollUtil.newHashSet(TestConst.BROWSER_CHROME);	
	private boolean failInterrupt = false;	
	private String tag = "default";	
	private int retryCount = 2;
	
	private String suiteYamlFileName;
	
	@SuppressWarnings("rawtypes")
	private Class[] caseClasses;

	/**
	 * 执行用例
	 */
	private List<ExecuteCaseModel> cases = new ArrayList<ExecuteCaseModel>();
	/**
	 * 根据tag对执行用例进行分类
	 */
	private Map<String, List<ExecuteCaseModel>> tagCases = new HashMap<String, List<ExecuteCaseModel>>();
	
	/**
	 * 报告处理器
	 */
	private Set<IReportManager> reportManagers = CollUtil.newLinkedHashSet();
	
	private static Object lock = new Object();
	
	/**
	 * 分别表示 finishCount,successCount,failCount, SkipCount
	 */
	private AtomicInteger[] testCounts;
	/**
	 * 本次执行的用例总数
	 */
	private int totalCount = 0;
	
	/**
	 * 实例化测试对象
	 * @param suiteYamlFileName 设定测试套件的yaml文件名称，不带.yaml后缀，在文件中定义执行规则
	 * @throws Exception 
	 */
	public WebTest(String suiteYamlFileName) throws Exception {
		super();
		logger.info("欢迎你使用易大师UI自动化测试框架，当前版本为[{}]", TestConst.FRAMEWORK_VERSION);
		this.suiteYamlFileName = suiteYamlFileName;
		parseSuiteYaml();
		init();
	}
	
	/**
	  * 实例化测试对象
	 * @param caseClasses 指定多个需要执行的Case类，根据类中用例方法上的UseCase注解规则来执行
	 * @throws Exception 
	 */
	@SuppressWarnings("rawtypes")
	public WebTest(Class ... caseClasses) throws Exception {
		super();
		logger.info("欢迎你使用易大师UI自动化测试框架，当前版本为[{}]", TestConst.FRAMEWORK_VERSION);
		this.caseClasses = caseClasses;
		parseCaseClasses();
		init();
	}

	/**
	 * 开始执行,有两种方法<br>
	 * 1、设置testsuite的yaml文件，在文件中定义执行规则<br>
	 * 2、指定执行的Case类，自动化根据类中方法上的注解规则来执行<br>
	 * 	两种都配置了优先使用yaml配置文件进行测试
	 * @throws Exception 
	 */
	public void start() {
		//判断当前有没有正在执行的测试任务		
		synchronized (lock) {
			while (GlobalTestConfig.testing.get()) {
				try {
					lock.wait(800);
				} catch (InterruptedException e) {
					logger.warn(e);
				}
			}
			GlobalTestConfig.testing.set(true);
		}
		
		logger.info("开始执行测试，测试用例数为{}个", totalCount);	

		TimeInterval interval = new TimeInterval();
		GlobalTestConfig.report = new SuiteReport();
		GlobalTestConfig.report.setTotalCount(totalCount);
		GlobalTestConfig.report.setTitle(testTitle);
		GlobalTestConfig.report.setEnv(GlobalTestConfig.ENV_INFO);
		GlobalTestConfig.report.setTestTime(DateUtil.now());
		GlobalTestConfig.report.setBrowserName(browserType);
		
		testCounts = new AtomicInteger[] {new AtomicInteger(0), new AtomicInteger(0), new AtomicInteger(0), new AtomicInteger(0)};
		//执行用例	
		if (GlobalTestConfig.ENV_INFO.isRemoteMode()) {
			//分布式执行			
			for (String tagKey:tagCases.keySet()) {
				ThreadUtil.execute(new Runnable() {					
					@Override
					public void run() {
						autoTest(tagCases.get(tagKey));	
					}
				});
			}			
			while (testCounts[0].get() < GlobalTestConfig.report.getTotalCount()) {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {}
			}	
		} else {
			//本地执行
			autoTest(cases);			
		}
		
		GlobalTestConfig.report.setSuccessCount(testCounts[1].get());
		GlobalTestConfig.report.setFailCount(testCounts[2].get());
		GlobalTestConfig.report.setSkipCount(testCounts[3].get());
		GlobalTestConfig.report.setEndTime(DateUtil.now());
		GlobalTestConfig.report.setUseTime(interval.intervalMs());	
		
		GlobalTestConfig.report.setReportName(GlobalTestConfig.report.getTitle() + "_" + DateUtil.format(new Date(), "yyyyMMddHHmmss"));
		GlobalTestConfig.report.setFinished(true);
		
		GlobalTestConfig.testing.set(false);
		
		manageReport();
		logger.info("测试完成");
	}
	
	/**
	 * 使用配置文件配置的定时任务规则
	 * @throws Exception 
	 */
	public String startCron() throws Exception {
		return startCron(GlobalTestConfig.ENV_INFO.getCronExpression());
	}
	
	/**
	 * 启动定时测试任务
	 * @param expression 传入定时规则表达式
	 * @throws Exception 
	 */
	public String startCron(String expression) throws Exception {
		if (!GlobalTestConfig.ENV_INFO.isCronEnabled()) {
			logger.warn("cron.enabled=false, 不能开启定时测试任务，请修改seleniumConfig.properties配置文件!");
			return "cron.enabled=false, 不能开启定时测试任务，请修改seleniumConfig.properties配置文件!";
		}
		
		logger.info("定时测试任务已开启, CronExpression[{}]", expression);
		start();
		GlobalTestConfig.cronTaskId = CronUtil.schedule(expression, new Task() {
		    @Override
		    public void execute() {
		    	try {
					start();					
				} catch (Exception e) {
					logger.error(e, "定时测试任务执行出错，请检查！");
				}
		    }
		});
			
		CronUtil.start();
		return "true";
	}
	
	/**
	 * 测试初始化
	 * @throws Exception
	 */
	private void init() throws Exception {
		if (totalCount == 0) {
			logger.info("可执行测试用例个数为0！");
			throw new Exception("当前无可执行的测试用例!");
		}			
		initPageObject();
	}
	
	/**
	 * 处理测试报告
	 */
	public void manageReport() {		
		for (IReportManager r:reportManagers) {
			logger.info("正在执行报告处理器 [{}]...", r.getClass().getName());
			try {
				r.manage(GlobalTestConfig.report);
				logger.info("报告处理器 [{}]执行成功!", r.getClass().getName());
			} catch (Exception e) {
				logger.error(e, "[{}] 报告处理器执行出错！", r.getClass().getName());
			}
			
		}
	}
	
	/**
	 * 测试结束清理环境
	 */
	public void clean() {	
		//关闭WebDriver
		for (String key:GlobalTestConfig.getTestRunningObject().getDrivers().keySet()) {
			WebDriver driver = GlobalTestConfig.getTestRunningObject().getDrivers().get(key);
			if (driver != null) {
				logger.info("关闭webdriver[{} for {}]", key, TestKit.getOsName());
				driver.quit();
			}
		}
		
		//清理temp文件夹		
		FileUtil.clean(TestConst.TEST_TEMP_FLODER);
		logger.info("清理temp文件夹[{}]成功!", TestConst.TEST_TEMP_FLODER);
		
		//执行每个DataModel的destory方法
		for (BaseDataModel d:GlobalTestConfig.getTestRunningObject().getDatas()) {
			d.destroyData();
		}
		//remove线程对象
		GlobalTestConfig.removeTestRunningObject();
	}
	
	/**
	 * 	自动化测试
	 * @param execuCaseModels
	 * @param finishCount
	 * @throws MalformedURLException
	 */
	private void autoTest(List<ExecuteCaseModel> execuCaseModels) {
		boolean skipFlag = false;
		
		for (int i = 0;i < execuCaseModels.size();i++) {
			ExecuteCaseModel ecm = execuCaseModels.get(i);			
			for (String browserName:ecm.getBrowserType()) {
				if (skipFlag) {
					//跳过执行
					testCounts[3].incrementAndGet();					
				} else {
					GlobalTestConfig.report.getBrowserName().add(browserName);
					
					testCounts[0].incrementAndGet();
					try {
						GlobalTestConfig.getTestRunningObject().setDriver(browserName);
					} catch (Exception e) {
						logger.error(e, "获取WebDriver[{} for {}]出错！", browserName, TestKit.getOsName());
						testCounts[3].incrementAndGet();
						continue;
					}					
					ecm.execute(browserName);
					if (!ecm.isSuccessFlag()) {
						if (ecm.isFailInterrupt()) {
							skipFlag = true;
						}
						testCounts[2].incrementAndGet();
						break;
					} else {
						testCounts[1].incrementAndGet();
					}
				}				
			}			
		}		
		clean();
	}
	
	/**
	 * 初始化所有PageModel类
	 * @throws Exception
	 */
	private void initPageObject() throws Exception {
		logger.info("正在初始化所有PageModel...");
		for (ExecuteCaseModel model:cases) {
			for (Object caseObj:model.getTargets()) {
				for (Field f:caseObj.getClass().getFields()) {
					if (BasePage.class.isAssignableFrom(f.getType())) {
						f.set(caseObj, ProxyUtil.proxy(ReflectUtil.newInstance(f.getType()), CreateStepReportAspect.class));
						((BasePage) f.get(caseObj)).initPageObject();
					} else if(BaseDataModel.class.isAssignableFrom(f.getType())) {
						f.set(caseObj, DataModelFactory.getDataModelInstance(f.getType()));
						continue;
					} else {
						continue;
					}
				}
			}
		}
		logger.info("PageModel初始化完成");
	}
	
	/**
	 * 根据yaml文件解析执行用例信息
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void parseSuiteYaml() throws Exception {
		if (StrUtil.isEmpty(suiteYamlFileName)) {
			return;
		}
		Map map = null;
		try {
			map = TestKit.parseYaml(GlobalTestConfig.ENV_INFO.getSuiteFolder() + "/" + suiteYamlFileName + ".yaml");
			
			if (map.get("browserType") != null) {
				browserType = new HashSet<String>(ConverterRegistry.getInstance().convert(List.class, map.get("browserType"), Arrays.asList(new String[]{map.get("browserType").toString()})));
			}
			
			failInterrupt = Convert.toBool(map.get("failInterrupt"), false);
			tag = Convert.toStr(map.get("tag"), "default");
			retryCount = Convert.toInt(map.get("retryCount"), 2);
			testTitle = Convert.toStr(map.get("title"), "Web自动化测试");
			
			//报告处理器
			List<String> reportManagerClass = (List<String>) map.get("reportManager");
			if (reportManagerClass != null && reportManagerClass.size() > 0) {
				for (String s:reportManagerClass) {
					try {
						IReportManager m = TestKit.parseReportManager(s);
						if (m != null) {
							reportManagers.add(m);
						}
					} catch (Exception e) {
						logger.warn(e, "测试报告处理器[{}]实例化失败,请检查！", s);
					}
				}
			}
			
			List<Map> cases = (List<Map>) map.get("cases");	
			for (Map m:cases) {
				//如果设置了enabled=false则忽略测试
				if (!Convert.toBool(m.get("enabled"), true)) {
					continue;
				}		
				ExecuteCaseModel caseModel = new ExecuteCaseModel();
				//获取测试用例类和需要执行的方法
				Object cs = m.get("method");
				List<String> css = ConverterRegistry.getInstance().convert(List.class, cs, Arrays.asList(new String[]{cs.toString()}));
				for (String s:css) {
					String methodName = ArrayUtil.get(s.split("\\."), -1);
					String className = s.substring(0, s.lastIndexOf("."));
					
					//实例化用例类
					Object caseObj = ReflectUtil.newInstance(className);						
					caseModel.getTargets().add(caseObj);					
					//获取用例方法
					caseModel.getMethods().add(ReflectUtil.getMethod(caseObj.getClass(), methodName));
				}				
				//获取其他属性
				caseModel.setBrowserType(browserType);
				if (m.get("browserType") != null) {
					caseModel.setBrowserType(new HashSet<String>(ConverterRegistry.getInstance().convert(List.class, m.get("browserType"), Arrays.asList(new String[]{m.get("browserType").toString()}))));
				}
				
				caseModel.setName(MapUtil.getStr(m, "name"));
				caseModel.setFailInterrupt(Convert.toBool(m.get("failInterrupt"), failInterrupt));
				caseModel.setRetryCount(Convert.toInt(m.get("retryCount"), retryCount));
				caseModel.setTag(Convert.toStr(m.get("tag"), tag));	
				
				if (!tagCases.containsKey(caseModel.getTag())) {
					tagCases.put(caseModel.getTag(), new ArrayList<ExecuteCaseModel>());
				}
				tagCases.get(caseModel.getTag()).add(caseModel);
				
				totalCount += caseModel.getBrowserType().size();
				this.cases.add(caseModel);
			}
			logger.info("测试用例配置文件{}.yaml解析完成", this.suiteYamlFileName);
		} catch (Exception e) {
			logger.error(e, "测试用例配置文件{}.yaml解析过程中出错", this.suiteYamlFileName);
			throw e;
		}
		
	}

	/**
	 * 根据用例类中的方法注解UseCase来解析执行用例情况
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void parseCaseClasses() throws Exception {
		if (caseClasses.length == 0) {
			return;
		}
		for (Class clz:caseClasses) {
			logger.info("解析测试用例执行类：{}", clz.getName());
			Object o = ReflectUtil.newInstance(clz);
			for (Method m:clz.getMethods()) {
				//必须有UseCase注解
				if (!m.isAnnotationPresent(UseCase.class)) {
					continue;
				}	
				//必须设定了enabled=true
				UseCase uc = m.getAnnotation(UseCase.class);
				if (!uc.enabled()) {
					continue;
				}
				
				ExecuteCaseModel caseModel = new ExecuteCaseModel();
				caseModel.setName(StrUtil.isBlank(uc.name()) ? m.getName() : uc.name());
				caseModel.setFailInterrupt(uc.failInterrupt());
				caseModel.setRetryCount(uc.retryCount());
				caseModel.setTag(uc.tag());
				caseModel.setBrowserType(uc.browserType().length == 0 ? browserType : CollUtil.newHashSet(uc.browserType()));
				
				caseModel.getTargets().add(o);
				caseModel.getMethods().add(m);
				
				if (!tagCases.containsKey(caseModel.getTag())) {
					tagCases.put(caseModel.getTag(), new ArrayList<ExecuteCaseModel>());
				}
				tagCases.get(caseModel.getTag()).add(caseModel);
				
				totalCount += caseModel.getBrowserType().size();
				this.cases.add(caseModel);
			}
		}
	}
	
	public void setTestTitle(String testTitle) {
		this.testTitle = testTitle;
	}
	
	public String getTestTitle() {
		return testTitle;
	}
	
	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}
	
	public int getRetryCount() {
		return retryCount;
	}

	public boolean isFailInterrupt() {
		return failInterrupt;
	}

	public void setFailInterrupt(boolean failInterrupt) {
		this.failInterrupt = failInterrupt;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public void setBrowserType(Set<String> browserType) {
		this.browserType = browserType;
	}
	
	public Set<String> getBrowserType() {
		return browserType;
	}

	public Set<IReportManager> getReportManagers() {
		return reportManagers;
	}
	
	/**
	 * 设置报告数据处理器，传入多个将会安装顺序执行
	 * @param reportManagers
	 */
	public void setReportManagers(IReportManager... reportManagers) {
		if (reportManagers != null && reportManagers.length > 0) {
			this.reportManagers.addAll(Arrays.asList(reportManagers));
		}
	}
	
	
}
