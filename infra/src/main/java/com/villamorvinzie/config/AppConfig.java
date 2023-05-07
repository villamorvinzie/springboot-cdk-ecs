package com.villamorvinzie.config;

import java.util.Optional;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

public class AppConfig {

    private static AppConfig APP_CONFIG;
    private Configuration config;

    private AppConfig(String env) throws ConfigurationException {
        String defaultProp = "application.properties";
        Configurations configs = new Configurations();
        Optional<String> optional = Optional.ofNullable(env);
        this.config = configs.properties(optional.orElse(defaultProp));
    }

    public static AppConfig build(String env) throws ConfigurationException {
        if (APP_CONFIG == null) {
            APP_CONFIG = new AppConfig(env);
        }
        return APP_CONFIG;
    }

    public Object getValue(String key) {
        return config.getProperty(key);
    }

    public String getStringValue(String key) {
        return config.getString(key);
    }

    public int getIntValue(String key) {
        return config.getInt(key);
    }
}
