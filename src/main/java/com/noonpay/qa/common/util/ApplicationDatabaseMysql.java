package com.noonpay.qa.common.util;

import java.sql.Connection;
import java.sql.DriverManager;

public class ApplicationDatabaseMysql {

	private static ApplicationDatabaseMysql instance;

	private Connection connection;

	private ApplicationDatabaseMysql() {
		String env = System.getProperty("env");

		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(EnviromentPropertyUtil.getPropertyValue(env + ".datasource.url"),
					EnviromentPropertyUtil.getPropertyValue(env + ".datasource.username"),
					EnviromentPropertyUtil.getPropertyValue(env + ".datasource.password"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static ApplicationDatabaseMysql getInstance() {
		if (instance == null) {
			synchronized (ApplicationDatabaseMysql.class) {
				if (instance == null) {
					instance = new ApplicationDatabaseMysql();
				}
			}
		}

		return instance;
	}
	
	public static Connection getConnection() {
		return getInstance().connection;
		
	}

}
