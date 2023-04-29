package com.villamorvinzie.demo.services;

import com.villamorvinzie.demo.dto.Weather;

public interface WeatherStackService {
    Weather getCurrent(String city);
}
