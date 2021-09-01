package com.noonpay.qa.common.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiResponse {

    private Map<String, String> headers = new HashMap<String, String>();
    private Map<String, String> expectedPayload = new HashMap<String, String>();
    private String statusCode;
    private String schema;
}
