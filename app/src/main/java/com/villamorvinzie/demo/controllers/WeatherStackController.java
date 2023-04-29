package com.villamorvinzie.demo.controllers;

import org.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.villamorvinzie.demo.dto.Weather;
import com.villamorvinzie.demo.services.WeatherStackService;

@RestController
@RequestMapping("weather")
public class WeatherStackController {

    private WeatherStackService weatherStackService;

    public WeatherStackController(WeatherStackService weatherStackService) {
        this.weatherStackService = weatherStackService;
    }

    @GetMapping(produces = "application/json")
    public Weather getCurrent(
            @RequestParam String city) {
        return weatherStackService.getCurrent(city);
    }
}
