package camel.msg.adapter.data;

import lombok.Data;

import java.util.ArrayList;

@Data
public class CurrentWeather {
    Coord CoordObject;
    ArrayList<Object> weather = new ArrayList<>();
    private String base;
    Main MainObject;
    private float visibility;
    Wind WindObject;
    Clouds CloudsObject;
    private float dt;
    Sys SysObject;
    private float timezone;
    private float id;
    private String name;
    private float cod;

    @Data
    public class Sys {
        private float type;
        private float id;
        private String country;
        private float sunrise;
        private float sunset;
    }

    @Data
    public class Clouds {
        private float all;
    }

    @Data
    public class Wind {
        private float speed;
        private float deg;
        private float gust;
    }

    @Data
    public class Main {
        private float temp;
        private float feels_like;
        private float temp_min;
        private float temp_max;
        private float pressure;
        private float humidity;
    }

    @Data
    public class Coord {
        private float lon;
        private float lat;
    }
}
