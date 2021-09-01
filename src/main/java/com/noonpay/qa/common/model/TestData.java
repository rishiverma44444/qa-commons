package com.noonpay.qa.common.model;

import com.noonpay.qa.common.custom.annotations.Api;
import com.noonpay.qa.common.enums.MethodType;
import com.noonpay.qa.common.enums.ServiceName;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


@JsonIgnoreProperties(ignoreUnknown = true)
public class TestData {

    private String className;
    private String endpoint;
    private ServiceName service;
    private MethodType method;

    private Map<String, ApiRequest> request = new HashMap<String, ApiRequest>();
    private Map<String, ApiResponse> response = new HashMap<String,ApiResponse >();

    public Map<String, ApiRequest> getRequest() {
        return request;
    }
    public MethodType getMethod() {
        return method;
    }

    public void setMethod(MethodType method) {
        this.method = method;
    }

    public void setRequest(Map<String, ApiRequest> request) {
        this.request = request;
    }

    public Map<String, ApiResponse> getResponse() {
        return response;
    }

    public void setResponse(Map<String, ApiResponse> response) {
        this.response = response;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public ServiceName getService() {
        return service;
    }

    public void setService(ServiceName service) {
        this.service = service;
    }

    public String getEndpoint() {

        String requestId = getRequestIdOfCallerMethod();
        String[] pathParams=request.get(requestId).getPathParams();

        if (pathParams == null || pathParams.length == 0) {
            return endpoint;
        } else {
            return String.format(endpoint, pathParams);
        }
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }
    private String getRequestIdOfCallerMethod() {
        StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
        String methodName = stacktrace[3].getMethodName();
        try {
            Method method = Class.forName(stacktrace[3].getClassName()).getDeclaredMethod(methodName, TestData.class);
            Api apiAnnotation = method.getAnnotation(Api.class);

            if (apiAnnotation != null) {
                return apiAnnotation.name();
            }
        } catch (NoSuchMethodException | SecurityException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

}
