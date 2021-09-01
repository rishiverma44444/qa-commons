package com.noonpay.qa.common.aspect;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.file.Files;
import java.util.Calendar;
import java.util.Map;
import com.noonpay.qa.common.model.ApiRequest;
import com.noonpay.qa.common.model.TestData;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.bson.Document;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.PropertyNamingStrategy;
import org.codehaus.jackson.type.TypeReference;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.noonpay.qa.common.custom.annotations.TestDataSetup;
import com.noonpay.qa.common.util.BackendAutomationDatabase;
import com.noonpay.qa.common.util.CommonUtils;
import com.noonpay.qa.common.util.EnviromentPropertyUtil;

@Aspect
public class DataAspect {

	@After("set(* *) && @annotation(com.noonpay.qa.common.custom.annotations.Data)")
	public void processBefore(JoinPoint joinPoint) {
		if (StringUtils.equals(joinPoint.getTarget().getClass().getName(), "com.noonpay.qa.common.test.BaseTest")) {
			return;
		}

		String endpoint = joinPoint.getTarget().getClass().getAnnotationsByType(TestDataSetup.class)[0].endpoint();
		System.out.println("populating data in class = " + joinPoint.getSignature().getDeclaringTypeName() + ", field = " + joinPoint.getSignature().getName()
				+ ". endpoint = " + endpoint);
		populateTestingData(joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getTarget(), joinPoint.getSignature().getName(), endpoint);
	}

	private void populateTestingData(String testClass, Object target, String field, String endpoint) {

		try {
			MongoDatabase mongoDatabase = BackendAutomationDatabase.getInstance().getMongoDatabase();
			String collectionName = EnviromentPropertyUtil.getPropertyValue("mongo.datasource.collection");
			MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName, Document.class);

			Document document = collection.find(Filters.eq("endpoint", endpoint)).first();
			if (document == null) {
				return;
			}
			ObjectMapper mapper = new ObjectMapper();
			mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
			TestData testData = mapper.readValue(document.toJson(), TestData.class);


			processHeaders(testData);
			processQueryParams(testData.getRequest());
			processPathParams(testData.getRequest());

			Field dataField = Class.forName(testClass).getDeclaredField("data");
			dataField.setAccessible(true);
			dataField.set(target, testData);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void processHeaders(TestData testData) throws JsonParseException, JsonMappingException, IOException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Field request = TestData.class.getDeclaredField("request");
		request.setAccessible(true);
		Map<String, ApiRequest> map = (Map<String, ApiRequest>) request.get(testData);
		for (Map.Entry<String, ApiRequest> entry : map.entrySet()) {
			ApiRequest requestType = entry.getValue();
			populateCommonHeaders(requestType.getHeaders());
			processHeaderVariables(requestType.getHeaders());

		}
	}

	private void processHeaderVariables(Map<String, String> headers) {
		for (Map.Entry<String, String> map : headers.entrySet()) {
			if (CommonUtils.isSpEx(map.getValue())) {
				String mapValue = ((String) map.getValue()).replace("#{", "").replace("}", "");
				String val = null;
				if (mapValue.endsWith("()")) {
					val = getProcessedMethodValue(mapValue);
				} else {
					String propKey = System.getProperty("env") + "." + mapValue;
					val = EnviromentPropertyUtil.getPropertyValue(propKey);
				}

				map.setValue(val);
			}
		}
	}

	private void populateCommonHeaders(Map<String, String> headers) throws JsonParseException, JsonMappingException, IOException {
		headers.put("Content-Type", "application/json");

		ClassLoader classLoader = ClassLoader.getSystemClassLoader();
		URL resourceUrl = classLoader.getResource("common-headers.json");

		if (resourceUrl == null) {
			return;
		}

		File file = new File(resourceUrl.getFile());
		Map<String, String> map = new ObjectMapper().readValue(new String(Files.readAllBytes(file.toPath())), new TypeReference<Map<String, String>>() {
		});

		for (Map.Entry<String, String> entry : map.entrySet()) {

			String value = entry.getValue();
			if (StringUtils.equals(value, "${now()}")) {
				value = String.valueOf(Calendar.getInstance().getTimeInMillis());
			}

			if (!headers.containsKey(entry.getKey())) {
				headers.put(entry.getKey(), value);
			}
		}
	}

	private String getProcessedMethodValue(String input) {
		if (StringUtils.equals(input, "timestamp()")) {
			return String.valueOf(Calendar.getInstance().getTimeInMillis());
		}

		return "#{" + input + "}";
	}

	private void processQueryParams(Map<String, ApiRequest> requestMap) {

		for (Map.Entry<String, ApiRequest> entry : requestMap.entrySet()) {
			ApiRequest request = entry.getValue();
			processHeaderVariables(request.getQueryParams());
		}

	}

	private void processPathParams(Map<String, ApiRequest> requestMap) {
		for (Map.Entry<String, ApiRequest> entry : requestMap.entrySet()) {
			ApiRequest request=entry.getValue();
			String[] pathParams=request.getPathParams();

			if(pathParams !=null && pathParams.length > 0) {
			for (int i = 0; i < pathParams.length; i++) {

				if (CommonUtils.isSpEx(pathParams[i])) {
					String mapValue = pathParams[i].replace("#{", "").replace("}", "");
					String val = null;
					if (mapValue.endsWith("()")) {
						val = getProcessedMethodValue(mapValue);

					} else {
						String propKey = System.getProperty("env") + "." + mapValue;
						val = EnviromentPropertyUtil.getPropertyValue(propKey);
					}
					pathParams[i] = val;
				}
			}
		}
		}
	}
}
