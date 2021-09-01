package com.noonpay.qa.common.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;


@Component
public class CommonUtils {

    public static boolean isSpEx(String input) {
        if (StringUtils.isEmpty(input)) {
            return false;
        }
        if (!input.startsWith("#{") || !input.endsWith("}")) {
            return false;
        }
        String key = input.replace("#{", "").replace("}", "");
        if (key.matches("^[a-zA-Z0-9.()_]*$")) {
            return true;
        }
        return false;
    }
}
