package com.noonpay.qa.common.connector;

import java.lang.reflect.Method;
import java.util.*;
import com.noonpay.qa.common.model.BaseResponse;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import com.noonpay.qa.common.builder.MessageBuilder;
import com.noonpay.qa.common.constants.CommonTestingConstants;
import com.noonpay.qa.common.util.CommonUtils;
import com.noonpay.qa.common.util.EnviromentPropertyUtil;
import io.restassured.RestAssured;
import io.restassured.response.Response;

@Component
public class RestAssuredClient {

	@Autowired
	private Environment environment;

	public BaseResponse getObject(String url, Map<String, String> headers) {
		BaseResponse baseResponse= new BaseResponse();
		Response response = RestAssured.given().log().all().headers(headers).when().get(url);
		baseResponse.setResponse(response);
		return baseResponse;
	}

	public BaseResponse getObject(String url, Map<String, String> headers,Map<String,String> queryParams) {
		BaseResponse baseResponse= new BaseResponse();
		Response response = RestAssured.given().log().all().headers(headers).queryParams(queryParams).when().get(url);
		baseResponse.setResponse(response);
		return baseResponse;
	}

	public BaseResponse postObject(String url, Map<String, String> headers, String requestBody, Properties properties) {
		BaseResponse baseResponse= new BaseResponse();
		String finalRequestBody = preProcessRequest(requestBody, properties, headers);
		Response response = RestAssured.given().log().all().headers(headers).body(finalRequestBody).when().post(url);
		baseResponse.setResponse(response);
		baseResponse.setFinalRequestBody(finalRequestBody);
		return baseResponse;
	}

	public BaseResponse putObject(String url, Map<String, String> headers, String requestBody, Properties properties) {
		BaseResponse baseResponse= new BaseResponse();
		String finalRequestBody = preProcessRequest(requestBody, properties, headers);
		Response response = RestAssured.given().log().all().headers(headers).body(finalRequestBody).when().put(url);
		baseResponse.setResponse(response);
		baseResponse.setFinalRequestBody(finalRequestBody);
		return baseResponse;
	}

	public BaseResponse deleteObject(String url, Map<String, String> headers, String requestBody, Properties properties) {
		BaseResponse baseResponse= new BaseResponse();
		String finalRequestBody = preProcessRequest(requestBody, properties, headers);
		Response response = RestAssured.given().log().all().headers(headers).body(finalRequestBody).when().delete(url);
		baseResponse.setResponse(response);
		baseResponse.setFinalRequestBody(finalRequestBody);
		return baseResponse;
	}

	private String preProcessRequest(String requestBody, Properties properties, Map<String, String> headers) {
		String finalRequestBody = MessageBuilder.buildStringMessage(requestBody, properties);
		finalRequestBody = processRequestVariables(finalRequestBody);
		populateChecksumHeader(finalRequestBody, headers);
		return finalRequestBody;
	}

	private String processRequestVariables(String input) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, Object> map = objectMapper.readValue(input, new TypeReference<Map<String, Object>>() {
			});
			Set<Map.Entry<String, Object>> sampleRequestEntrySet = map != null ? map.entrySet() : new HashSet<Map.Entry<String, Object>>();

			for (Map.Entry<String, Object> entry : sampleRequestEntrySet) {
				if (entry.getValue() instanceof String && CommonUtils.isSpEx((String) entry.getValue())) {
					String propKey = System.getProperty("env") + "." + ((String) entry.getValue()).replace("#{", "").replace("}", "");
					String val = null;

					if (propKey.endsWith("()")) {
					} else {
						val = EnviromentPropertyUtil.getPropertyValue(propKey);
					}

					entry.setValue(val);
				}
			}

			return objectMapper.writeValueAsString(map);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private void populateChecksumHeader(String finalRequestBody, Map<String, String> headers) {
		if (!headers.containsKey(CommonTestingConstants.CHECKSUM_HEADER)) {
			return;
		}

		String checksumClass = environment.getProperty("checksum.class");
		if (StringUtils.isEmpty(checksumClass)) {
			return;
		}

		try {
			Class klass = Class.forName(checksumClass);
			Method method = klass.getMethod("calculateChecksum", String.class, Map.class);
			String checksum = (String) method.invoke(null, finalRequestBody, headers);

			headers.put(CommonTestingConstants.CHECKSUM_HEADER, checksum);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
