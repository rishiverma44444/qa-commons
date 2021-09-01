package com.noonpay.qa.common.model;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiRequest {

    private Map<String, String> headers = new HashMap<String, String>();
    private Map<String, String> queryParams = new HashMap<String, String>();
    private String[] pathParams = new String[]{};;
    private Properties dynamicPayload;


    public Properties getDynamicPayload() {
        return dynamicPayload;
    }

    public void setDynamicPayload(JsonNode jn) {
        if (jn == null) {
            return;
        }
        Properties properties = new Properties();
        Map<String, String> map = new ObjectMapper().convertValue(jn, new TypeReference<Map<String, String>>() {
        });
        for (final String key : map.keySet()) {
            String value = map.get(key);
            if (!StringUtils.isEmpty(value)) {
                properties.put(key, value.toString());
            }
        }
        this.dynamicPayload = properties;
    }

    public String[] getPathParams() {
        return pathParams;
    }

    public void setPathParams(String[] path_params) {
        this.pathParams = path_params;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(Map<String, String> queryParams) {
        this.queryParams = queryParams;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

}
