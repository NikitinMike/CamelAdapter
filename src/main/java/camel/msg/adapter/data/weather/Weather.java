package camel.msg.adapter.data.weather;

import lombok.Data;

@Data
public class Weather {
    int id; // =803;
    String main; // =Clouds;
    String description; //=облачно с прояснениями,
    String icon; //=04n
}
