package camel.msg.adapter.data;

import camel.msg.adapter.data.weather.*;
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
    Clouds clouds;
    float dt;
    Sys sys;
    float timezone;
    float id;
    String name;
    float cod;
}
