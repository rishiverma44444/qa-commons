package com.noonpay.qa.common.testing.strategy.impl;

import com.noonpay.qa.common.testing.strategy.TestingStrategy;

public class IntegrationTestingStrategy implements TestingStrategy {

	private static IntegrationTestingStrategy instance;

	private IntegrationTestingStrategy() {

	}

	public static IntegrationTestingStrategy getInstance() {
		if (instance == null) {
			synchronized (IntegrationTestingStrategy.class) {
				if (instance == null) {
					instance = new IntegrationTestingStrategy();
				}
			}
		}

		return instance;
	}

	public void processTearUpData(String testClass, String env) {
		

	}

	public void processTearDownData(String testClass, String env) {
		// TODO Auto-generated method stub

	}

}
