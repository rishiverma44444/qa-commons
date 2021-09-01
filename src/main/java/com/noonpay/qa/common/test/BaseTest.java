package com.noonpay.qa.common.test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import com.noonpay.qa.common.connector.RestAssuredClient;
import com.noonpay.qa.common.constants.CommonTestingConstants;
import com.noonpay.qa.common.enums.ServiceName;
import com.noonpay.qa.common.model.ApiRequest;
import com.noonpay.qa.common.model.TestData;
import com.noonpay.qa.common.util.APIUrlProvider;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.DataProvider;
import com.noonpay.qa.common.config.TestConfig;
import com.noonpay.qa.common.custom.annotations.Api;
import com.noonpay.qa.common.custom.annotations.ApiTest;
import com.noonpay.qa.common.custom.annotations.Data;

@ContextConfiguration(classes = { TestConfig.class })
public class BaseTest extends AbstractTestNGSpringContextTests {

	@Autowired
	protected APIUrlProvider apiUrlProvider;

	@Autowired
	protected RestAssuredClient restAssuredClient;

	@Data
	TestData data = null;

	protected Map<String, Object> apiResponseMap = new HashMap<String, Object>();

	@DataProvider(name = "dataProvider")
	protected Object[][] dataProvider(Method method) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, SecurityException,
			ClassNotFoundException, NoSuchMethodException {
		Method[] declaredMethods = this.getClass().getDeclaredMethods();
		ApiTest testMethod = method.getAnnotation(ApiTest.class);
		if (apiResponseMap.containsKey(testMethod.api())) {
			return new Object[][] { { apiResponseMap.get(testMethod.api()) } };
		}

		for (Method m : declaredMethods) {
			Api api = m.getAnnotation(Api.class);
			if (api != null && StringUtils.equals(api.name(), testMethod.api())) {
				m.setAccessible(true);
				Object response = m.invoke(this, data);
				apiResponseMap.put(api.name(), response);
				break;
			}
		}

		return new Object[][] { { apiResponseMap.get(testMethod.api()) } };
	}
	
	@AfterClass(alwaysRun = true)
	public void afterClass() {
		
	}

	public static ApiRequest getRequestId(TestData data)  {
		StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
		String methodName = stacktrace[2].getMethodName();
		try {
			Method method = Class.forName(stacktrace[2].getClassName()).getDeclaredMethod(methodName, TestData.class);
			Api apiAnnotation = method.getAnnotation(Api.class);

			if (apiAnnotation != null) {
				return data.getRequest().get(apiAnnotation.name());
			}
		} catch (ClassNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getRequestBody(ServiceName serviceName)  {
		StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
		String fullQualifiedClassName = stacktrace[2].getClassName();
		String className=fullQualifiedClassName.substring(fullQualifiedClassName.lastIndexOf(".") + 1);
		String contents ="";
		try {
			contents=new String(Files.readAllBytes(Paths.get(CommonTestingConstants.REQUEST_BODY_FOLDER+serviceName+
					File.separator+className)));
		} catch (IOException e) {
			Assert.fail("api request not found for testcase");
		}
		return contents.replace("\n","");
	}
}
