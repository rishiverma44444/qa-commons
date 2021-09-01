package com.noonpay.qa.common.testing.strategy;

public interface TestingStrategy {

	void processTearUpData(String testClass, String env);

	void processTearDownData(String testClass, String env);

}
