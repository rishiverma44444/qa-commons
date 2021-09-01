package com.noonpay.qa.common.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import com.noonpay.qa.common.connector.RestAssuredClient;
import com.noonpay.qa.common.util.APIUrlProvider;


@Configuration
@PropertySource("classpath:${env}-application.properties")
public class TestConfig {

    @Bean
    public APIUrlProvider apiUrlProvider() {
        return new APIUrlProvider();
    }
    @Bean
    public RestAssuredClient restAssuredClient() {
        return new RestAssuredClient();
    }
}
