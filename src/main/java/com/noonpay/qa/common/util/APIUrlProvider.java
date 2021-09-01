package com.noonpay.qa.common.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class APIUrlProvider {

	@Autowired
	private Environment environment;

	public String getUrl(String endpoint) {
		String env = System.getProperty("env");
		return environment.getProperty("api.hostname." + env) + endpoint;
	}

}
