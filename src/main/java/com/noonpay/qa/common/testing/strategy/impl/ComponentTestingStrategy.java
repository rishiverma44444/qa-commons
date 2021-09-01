package com.noonpay.qa.common.testing.strategy.impl;

import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.commons.lang3.StringUtils;

import com.noonpay.qa.common.testing.strategy.TestingStrategy;
import com.noonpay.qa.common.util.EnviromentPropertyUtil;
import com.noonpay.qa.common.util.ScriptRunner;

public class ComponentTestingStrategy implements TestingStrategy {

	private static ComponentTestingStrategy instance;

	private ComponentTestingStrategy() {

	}

	public static ComponentTestingStrategy getInstance() {
		if (instance == null) {
			synchronized (ComponentTestingStrategy.class) {
				if (instance == null) {
					instance = new ComponentTestingStrategy();
				}
			}
		}

		return instance;
	}

	public void processTearUpData(String testClass, String env) {

		String fileName = "db/" + StringUtils.replace(testClass, ".java", "") + ".sql";
		executeDatabaseScript(fileName, env);
		// content = new String(Files.readAllBytes(file.toPath()));

	}

	public void processTearDownData(String testClass, String env) {
		String fileName = "db/" + StringUtils.replace(testClass, ".java", "") + "_teardown.sql";
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

			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection(EnviromentPropertyUtil.getPropertyValue(env + ".datasource.url"),
					EnviromentPropertyUtil.getPropertyValue(env + ".datasource.username"), EnviromentPropertyUtil.getPropertyValue(env + ".datasource.password"));

			ScriptRunner scriptRunner = ScriptRunner.getInstance();
			scriptRunner.runScript(new FileReader(file));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
