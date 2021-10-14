package camel.msg.adapter.data.weather;

import lombok.Data;

@Data
public class Sys {
    float type;
    float id;
    String country;
    float sunrise;
    float sunset;
}
