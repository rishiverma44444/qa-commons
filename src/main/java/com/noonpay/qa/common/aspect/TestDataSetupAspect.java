package com.noonpay.qa.common.aspect;

import java.io.File;
import java.io.FileReader;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import com.noonpay.qa.common.custom.annotations.TestDataSetup;
import com.noonpay.qa.common.util.ScriptRunner;

@Aspect
public class TestDataSetupAspect {

	@Before("@annotation(com.noonpay.qa.common.custom.annotations.TestDataSetup)")
	public void processBefore(JoinPoint joinPoint) {
		System.out.println("checking tearup data for " + joinPoint.getSignature().getDeclaringType());
		String tearupSql = ((TestDataSetup) joinPoint.getSourceLocation().getWithinType().getAnnotationsByType(TestDataSetup.class)[0]).tearup();

		// String testingType = System.getProperty("groups");
		String env = System.getProperty("env");
		/*TestingStrategy testingStrategy = null;

		if (StringUtils.equals(testingType, "component")) {
			testingStrategy = ComponentTestingStrategy.getInstance();
			testingStrategy.processTearUpData(joinPoint.getSourceLocation().getFileName(), env);
		}*/
		
		processTearUpData(tearupSql, env);
	}
	
	// @After("@annotation(com.noonpay.dataprovider.custom.annotations.TestDataSetup)")
	@After("@annotation(org.testng.annotations.AfterClass)")
	public void processAfter(JoinPoint joinPoint) {
		System.out.println("checking teardown data for " + joinPoint.getSignature().getDeclaringType());
		
		// String teardownSql = ((TestDataSetup) joinPoint.getSourceLocation().getWithinType().getAnnotationsByType(TestDataSetup.class)[0]).teardown();
		String teardownSql = joinPoint.getTarget().getClass().getAnnotationsByType(TestDataSetup.class)[0].teardown();

		// String testingType = System.getProperty("groups");
		String env = System.getProperty("env");
		// TestingStrategy testingStrategy = null;

		/* if (StringUtils.equals(testingType, "component")) {
			testingStrategy = ComponentTestingStrategy.getInstance();
			testingStrategy.processTearDownData(joinPoint.getSourceLocation().getFileName(), env);
		} else if (StringUtils.equals(testingType, "integration")) {
			
		} */

		processTearDownData(teardownSql, env);
	}
	
	public void processTearUpData(String sqlFile, String env) {
		if(StringUtils.isEmpty(sqlFile)) {
			return;
		}

		String fileName = "db/tearup/" + sqlFile;
		executeDatabaseScript(fileName, env);
		// content = new String(Files.readAllBytes(file.toPath()));
	}
	
	public void processTearDownData(String sqlFile, String env) {
		if(StringUtils.isEmpty(sqlFile)) {
			return;
		}
		
		String fileName = "db/teardown/" + sqlFile;
		executeDatabaseScript(fileName, env);
	}
	
	private void executeDatabaseScript(String fileName, String env) {
		try {
			ClassLoader classLoader = ClassLoader.getSystemClassLoader();
			URL resourceUrl = classLoader.getResource(fileName);
			if (resourceUrl == null) {
				System.out.println("No sql found with name " + fileName);
				return;
			}

			System.out.println("Executing sql file " + fileName);
			File file = new File(resourceUrl.getFile());

			ScriptRunner scriptRunner = ScriptRunner.getInstance();
			scriptRunner.runScript(new FileReader(file));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
