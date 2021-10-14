package camel.msg.adapter.data.weather;

import lombok.Data;

@Data
public class Main {
    float temp;
    float feels_like;
    float temp_min;
    float temp_max;
    float pressure;
    float humidity;
    float sea_level;
    float grnd_level;
}
