package com.villamorvinzie.demo.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "weatherstack")
@Getter
@Setter
public class WeatherStackConfig {
    private String url;
    private String key;
}
