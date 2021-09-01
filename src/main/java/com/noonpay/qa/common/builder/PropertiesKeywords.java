package com.noonpay.qa.common.builder;

public enum PropertiesKeywords {

    GENERATE_WORD_REGEX("generate_word\\(\\d+\\)");
    private String key;

    private PropertiesKeywords(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}
