package com.villamorvinzie.demo.mappers;

import java.util.function.Function;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.villamorvinzie.demo.dto.Weather;

import lombok.extern.log4j.Log4j2;

@Component
public class WeatherDtoMapper implements Function<String, Weather> {

    @Override
    public Weather apply(String str) {
        JSONObject obj = new JSONObject(str);
        JSONObject location = obj.getJSONObject("location");
        JSONObject current = obj.getJSONObject("current");
        return new Weather(location.getString("name"), location.getString("country"),
                current.getInt("temperature"));
    }

}
