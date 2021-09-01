package com.noonpay.qa.common.util;

import java.util.Arrays;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

public class BackendAutomationDatabase {

	private static BackendAutomationDatabase instance;

	private MongoDatabase mongoDatabase;

	private BackendAutomationDatabase() {
		String host = "mongo.datasource.host." + System.getProperty("env");
		String port = "mongo.datasource.port." + System.getProperty("env");
		ServerAddress address = new ServerAddress(EnviromentPropertyUtil.getPropertyValue(host),
				Integer.parseInt(EnviromentPropertyUtil.getPropertyValue(port)));

		MongoCredential credential = MongoCredential.createCredential(EnviromentPropertyUtil.getPropertyValue("mongo.datasource.user"),
				EnviromentPropertyUtil.getPropertyValue("mongo.datasource.db"),
				EnviromentPropertyUtil.getPropertyValue("mongo.datasource.password").toCharArray());

		MongoClient mongo = new MongoClient(address, Arrays.asList(credential));
		mongoDatabase = mongo.getDatabase(EnviromentPropertyUtil.getPropertyValue("mongo.datasource.db"));
	}

	public static BackendAutomationDatabase getInstance() {
		if (instance == null) {
			synchronized (BackendAutomationDatabase.class) {
				if (instance == null) {
					instance = new BackendAutomationDatabase();
				}
			}
		}

		return instance;
	}

	public MongoDatabase getMongoDatabase() {
		return mongoDatabase;
	}

}
