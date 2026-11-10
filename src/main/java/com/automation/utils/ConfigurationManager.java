package com.automation.utils;

import java.io.InputStream;
import java.util.Properties;

public class ConfigurationManager {
    private static Properties properties = new Properties();

    static {
        String env = System.getProperty("env", "qa");
        String fileName = "config/" + env + ".properties";
        try (InputStream input = ConfigurationManager.class.getClassLoader().getResourceAsStream(fileName)) {
            if (input != null) {
                properties.load(input);
            } else {
                properties.setProperty("base.url", "https://restful-booker.herokuapp.com");
            }
        } catch (Exception e) {
            properties.setProperty("base.url", "https://restful-booker.herokuapp.com");
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key, "https://restful-booker.herokuapp.com");
    }
}