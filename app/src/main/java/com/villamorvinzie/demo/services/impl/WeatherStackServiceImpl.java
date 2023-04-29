package com.villamorvinzie.demo.services.impl;

import java.util.HashMap;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.villamorvinzie.demo.configs.WeatherStackConfig;
import com.villamorvinzie.demo.dto.Weather;
import com.villamorvinzie.demo.mappers.WeatherDtoMapper;
import com.villamorvinzie.demo.services.WeatherStackService;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class WeatherStackServiceImpl implements WeatherStackService {

    private WeatherStackConfig weatherStackConfig;
    private RestTemplate restTemplate;
    private WeatherDtoMapper weatherDtoMapper;

    public WeatherStackServiceImpl(WeatherStackConfig weatherStackConfig, RestTemplate restTemplate,
            WeatherDtoMapper weatherDtoMapper) {
        this.weatherStackConfig = weatherStackConfig;
        this.restTemplate = restTemplate;
        this.weatherDtoMapper = weatherDtoMapper;
    }

    @Override
    public Weather getCurrent(String city) {
        String responseBody = restTemplate.getForObject(
                String.format("%s/%s?access_key=%s&query=%s", weatherStackConfig.getUrl(), "current",
                        weatherStackConfig.getKey(), city),
                String.class);

        return Optional.ofNullable(responseBody)
                .map(weatherDtoMapper)
                .get();
    }

}
