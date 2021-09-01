package com.noonpay.qa.common.model;

import io.restassured.response.Response;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseResponse {
    private Response response;
    private String finalRequestBody;
}
