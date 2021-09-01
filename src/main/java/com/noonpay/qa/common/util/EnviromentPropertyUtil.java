package com.noonpay.qa.common.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

public class EnviromentPropertyUtil {

    private static Properties prop;

    static {
        try {
            String env = System.getProperty("env");
            loadPropertiesByEnv(env);
            printProperties(prop);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadPropertiesByEnv(String env) throws FileNotFoundException, IOException {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        InputStream inputStream = null;
        prop = new Properties();
        inputStream = new FileInputStream(classLoader.getResource(env + "-application.properties").getFile());
        prop.load(inputStream);
    }

    public static void printProperties(Properties prop) {
        System.out.println("Printing environment properties");
        Enumeration keys = prop.keys();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            String value = (String) prop.get(key);
            System.out.println(key + ": " + value);
        }
    }

    public static String getPropertyValue(String key) {
        return prop.getProperty(key);
    }

}
