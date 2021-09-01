package com.noonpay.qa.common.builder;

import java.util.Map.Entry;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.noonpay.qa.common.util.GenerationUtil;

public class GenerateProcessor implements PropertiesProcessor {

    @Override
    public Properties process(Properties in) {
        Properties out = new Properties();
        for (Entry<Object, Object> entry : in.entrySet()) {
            Matcher wordMatcher = Pattern.compile(PropertiesKeywords.GENERATE_WORD_REGEX.getKey()).matcher(entry.getValue().toString());

            {
                if (wordMatcher.find()) {
                    String toReplace = wordMatcher.group();
                    Matcher tmpMatcher = Pattern.compile("\\d+").matcher(toReplace);
                    tmpMatcher.find();
                    String length = tmpMatcher.group();
                    out.put(entry.getKey(), entry.getValue().toString().replace(toReplace, GenerationUtil.generateWord(Integer.parseInt(length))));
                } else {
                    out.put(entry.getKey(), entry.getValue());
                }
            }
        }
        return out;
    }
}
