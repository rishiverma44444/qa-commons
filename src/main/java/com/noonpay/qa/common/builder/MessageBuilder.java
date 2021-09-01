package com.noonpay.qa.common.builder;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Properties;

public class MessageBuilder {

    private static Configuration freemarkerConfiguration;

    static {
        freemarkerConfiguration = new Configuration();
        freemarkerConfiguration.setTemplateLoader(new ClassTemplateLoader(MessageBuilder.class, "/"));
    }

    public final static synchronized String buildStringMessage(String templatePath, Properties... propertiesArr) {
        Template template;
        try {
            template = new Template("name", new StringReader(templatePath),
                    new Configuration());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Properties resultProperties = new Properties();
        for (Properties properties : propertiesArr) {
        	if(properties != null) {
        		resultProperties.putAll(properties);
        	}
        }
        
        StringWriter sw = new StringWriter();
        try {
            template.process(new GenerateProcessor().process(resultProperties), sw);
        } catch (TemplateException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                sw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sw.getBuffer().toString();
    }
}
