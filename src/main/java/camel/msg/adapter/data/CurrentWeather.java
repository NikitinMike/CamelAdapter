package camel.msg.adapter.data;

import camel.msg.adapter.data.weather.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.ArrayList;

@Data
public class CurrentWeather {
    Coord coord;
    ArrayList<Object> weather = new ArrayList<>();
    String base;
    Main main;
    float visibility;
    Wind wind;
    @JsonIgnoreProperties(ignoreUnknown = true)
    Rain rain;
    @JsonIgnoreProperties(ignoreUnknown = true)
    Snow snow;
    Clouds clouds;
    float dt;
    Sys sys;
    float timezone;
    float id;
    String name;
    float cod;
}
